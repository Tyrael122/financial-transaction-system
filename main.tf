provider "aws" {
  region = "us-east-1"  # Change to your preferred region
}

# ========================
# IAM Role for Lambda
# ========================
resource "aws_iam_role" "lambda_role" {
  name = "java_lambda_execution_role"

  assume_role_policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Action = "sts:AssumeRole",
        Effect = "Allow",
        Principal = {
          Service = "lambda.amazonaws.com"
        }
      }
    ]
  })
}

resource "aws_iam_role_policy_attachment" "lambda_basic_execution" {
  role       = aws_iam_role.lambda_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole"
}

resource "aws_iam_role_policy" "lambda_sqs_access" {
  name = "lambda_sqs_access"
  role = aws_iam_role.lambda_role.id

  policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Effect = "Allow",
        Action = [
          "sqs:ReceiveMessage",
          "sqs:DeleteMessage",
          "sqs:GetQueueAttributes"
        ],
        Resource = aws_sqs_queue.transactions_queue.arn
      }
    ]
  })
}

resource "aws_iam_role_policy" "lambda_sns_publish" {
  name = "lambda_sns_publish"
  role = aws_iam_role.lambda_role.id

  policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Effect = "Allow",
        Action = [
          "sns:Publish",
          "sns:CreateTopic", # Added CreateTopic permission
          "sns:GetTopicAttributes"  # Often needed for topic operations
        ],
        Resource = aws_sns_topic.transactions_topic.arn
      },
      {
        Effect = "Allow",
        Action = [
          "sns:ListTopics"  # Often needed for discovery operations
        ],
        Resource = "*"
      }
    ]
  })
}

# ========================
# HTTP API Gateway + Lambda (Java)
# ========================
resource "aws_apigatewayv2_api" "transactions_http_api" {
  name          = "NubankAPI"
  protocol_type = "HTTP"
  description   = "HTTP API Gateway for transaction processing"
}

resource "aws_apigatewayv2_route" "transaction_route" {
  api_id    = aws_apigatewayv2_api.transactions_http_api.id
  route_key = "POST /api/transaction"
  target    = "integrations/${aws_apigatewayv2_integration.lambda_integration.id}"
}

resource "aws_apigatewayv2_integration" "lambda_integration" {
  api_id           = aws_apigatewayv2_api.transactions_http_api.id
  integration_type = "AWS_PROXY"

  connection_type    = "INTERNET"
  description        = "Lambda integration"
  integration_method = "POST"
  integration_uri    = aws_lambda_function.api_lambda.invoke_arn
}

# Java Lambda for API Gateway
resource "aws_lambda_function" "api_lambda" {
  function_name = "CheckingAccountService"
  role          = aws_iam_role.lambda_role.arn
  handler       = "com.amazonaws.serverless.proxy.spring.SpringDelegatingLambdaContainerHandler"
  runtime = "java21"  # or "java17" if using Java 17
  memory_size   = 512
  timeout       = 30

  # Replace with your JAR file (uploaded via S3 or local)
  # s3_bucket = "your-lambda-bucket"
  # s3_key    = "api-lambda.jar"
  # OR use local file (for testing):
  filename = "CheckingAccountService/target/CheckingAccountService-0.0.1-SNAPSHOT-aws.jar"

  environment {
    variables = {
      MAIN_CLASS = "org.contoso.checkingaccount.CheckingAccountApplication"
    }
  }
}

resource "aws_lambda_permission" "apigw_lambda" {
  statement_id  = "AllowHTTPAPIGatewayInvoke"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.api_lambda.function_name
  principal     = "apigateway.amazonaws.com"
  source_arn    = "${aws_apigatewayv2_api.transactions_http_api.execution_arn}/*/*"
}

# ========================
# SNS Topic + SQS Queue
# ========================
resource "aws_sns_topic" "transactions_topic" {
  name = "transactionstopic"
}

resource "aws_sqs_queue" "transactions_queue" {
  name = "email-notification-queue"
}

resource "aws_sns_topic_subscription" "sqs_subscription" {
  topic_arn = aws_sns_topic.transactions_topic.arn
  protocol  = "sqs"
  endpoint  = aws_sqs_queue.transactions_queue.arn
}

resource "aws_sqs_queue_policy" "allow_sns" {
  queue_url = aws_sqs_queue.transactions_queue.id
  policy    = data.aws_iam_policy_document.sqs_policy.json
}

data "aws_iam_policy_document" "sqs_policy" {
  statement {
    actions = ["sqs:SendMessage"]
    resources = [aws_sqs_queue.transactions_queue.arn]
    principals {
      type = "Service"
      identifiers = ["sns.amazonaws.com"]
    }
    condition {
      test     = "ArnEquals"
      variable = "aws:SourceArn"
      values = [aws_sns_topic.transactions_topic.arn]
    }
  }
}

# ========================
# Java Lambda for SQS
# ========================
resource "aws_lambda_function" "sqs_lambda" {
  function_name = "EmailNotificationService"
  role          = aws_iam_role.lambda_role.arn
  handler = "org.springframework.cloud.function.adapter.aws.FunctionInvoker::handleRequest"
  # Adjust to your Java handler
  runtime = "java21"  # or "java17"
  memory_size   = 512
  timeout       = 30

  # Replace with your JAR file (uploaded via S3 or local)
  # s3_bucket = "your-lambda-bucket"
  # s3_key    = "sqs-lambda.jar"
  # OR use local file (for testing):
  filename = "EmailNotificationService/target/EmailNotificationService-0.0.1-SNAPSHOT-aws.jar"
}

resource "aws_lambda_event_source_mapping" "sqs_trigger" {
  event_source_arn = aws_sqs_queue.transactions_queue.arn
  function_name    = aws_lambda_function.sqs_lambda.arn
  batch_size       = 10  # Adjust as needed
}

# ========================
# Deploy HTTP API Gateway
# ========================
resource "aws_apigatewayv2_stage" "default_stage" {
  api_id      = aws_apigatewayv2_api.transactions_http_api.id
  name        = "$default"
  auto_deploy = true
}

# 1. Create DynamoDB table
resource "aws_dynamodb_table" "bank_account" {
  name           = "bank_account"
  billing_mode   = "PROVISIONED"
  hash_key = "accountId"  # Partition key
  read_capacity  = 5
  write_capacity = 5

  attribute {
    name = "accountId"
    type = "S"  # String
  }
}

# 2. Grant ONLY the API Lambda access to DynamoDB
resource "aws_iam_role_policy" "api_lambda_dynamodb" {
  name = "api_lambda_dynamodb_access"
  role = aws_iam_role.lambda_role.id  # Explicitly targets the API Lambda's role
  policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Effect = "Allow",
        Action = [
          "dynamodb:GetItem",
          "dynamodb:PutItem",
          "dynamodb:UpdateItem",
          "dynamodb:DeleteItem"
        ],
        Resource = aws_dynamodb_table.bank_account.arn
      }
    ]
  })
}