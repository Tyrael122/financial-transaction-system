# Event-Driven Financial Transactions System

## Overview

This system handles checking account transactions such as deposits, withdrawals, and balance updates using a serverless architecture. The entire infrastructure is provisioned using Terraform for infrastructure-as-code (IaC) management. The system uses AWS Lambda for backend processing, API Gateway for triggering transactions, DynamoDB for data storage, and SNS to publish events for real-time notifications via email and WhatsApp/SMS.

---

## Architecture Overview

- **AWS Lambda**: Used for backend processing of transactions (deposit, withdrawal, balance check).
- **API Gateway**: Exposes HTTP endpoints for initiating user transactions.
- **DynamoDB**: Stores checking account data (balance, transaction history).
- **SNS**: Publishes transaction events (e.g., deposit, withdrawal) to notify users.
- **Lambda Notification Services**: AWS Lambda functions to send notifications via email and SMS/WhatsApp using SNS events.
- **Terraform**: Manages all AWS resources as code for consistent deployments

---

## Workflow

1. **User Initiates a Transaction**:\
   Users make requests (e.g., deposit or withdrawal) via the **API Gateway** (HTTP endpoints).

2. **Triggering AWS Lambda (CheckingAccountService)**:\
   The API Gateway triggers a **Lambda function** (`CheckingAccountService`) to process the transaction. This involves updating the user’s account balance in **DynamoDB**.

3. **Publishing Events to SNS**:\
   After processing the transaction (deposit or withdrawal), the **Lambda function** publishes an event (e.g., `MoneyDeposited`, `MoneyWithdrawn`) to an **SNS Topic**.

4. **Notification Services**:

    - **Email Notification Service**: A **Lambda function** subscribes to the SNS topic and sends an email to the user (using custom SMTP or an email provider).
    - **WhatsApp/SMS Notification Service**: Another **Lambda function** subscribes to the SNS topic and sends an SMS or WhatsApp notification (using Twilio or WhatsApp API).

---

## API Endpoints

### **POST /deposit**

Initiates a deposit transaction.

- **Request Body**:
  ```json
  {
    "accountId": "123456789",
    "amount": 1000
  }
  ```
- **Response**:
  ```json
  {
    "message": "Deposit successful.",
    "newBalance": 5000
  }
  ```

### **POST /withdraw**

Initiates a withdrawal transaction.

- **Request Body**:
  ```json
  {
    "accountId": "123456789",
    "amount": 500
  }
  ```
- **Response**:
  ```json
  {
    "message": "Withdrawal successful.",
    "newBalance": 4500
  }
  ```

---

## Notifications

When a transaction (deposit/withdrawal) occurs, the system will trigger notifications through **SNS**.

- **Email Notification**: Sent to the user’s registered email address.
- **SMS/WhatsApp Notification**: Sent via Twilio or WhatsApp API.

### Example Email:

- Subject: **Transaction Confirmation**
    - "Your deposit of \$1000 was successful. Your new balance is \$5000."

### Example SMS/WhatsApp:

- "Alert: \$500 withdrawn from your account. Available balance: \$4500."

---

## Deployment

1. **Lambda Deployment**:\
   Use the **AWS Lambda Console** or **AWS CLI** to deploy your functions.

2. **API Gateway Deployment**:\
   Deploy the API Gateway to **stage** to expose the API endpoints.

3. **SNS and Subscriptions**:\
   Create the SNS topic and subscribe the Lambda functions for email and SMS notifications.

---

## Monitoring

- **CloudWatch**: Use **CloudWatch Logs** to monitor Lambda execution and diagnose errors.
- **API Gateway Logs**: Enable logging to monitor incoming HTTP requests and responses.

---

## Cost Considerations

- **Lambda**: You pay only for the execution time (compute).
- **API Gateway**: Charged based on the number of requests.
- **DynamoDB**: Charges are based on read/write capacity or on-demand requests.
- **SNS**: Based on the number of messages sent.

---

## Next Steps

1. Extend the system by adding more transaction types (e.g., transfers).
2. Implement fraud detection or transaction limits.
3. Integrate other notification services (e.g., push notifications).
4. Monitor and optimize performance based on traffic patterns.
