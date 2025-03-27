package org.contoso.checkingaccount.core.models;

import lombok.Data;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.math.BigDecimal;

@Data
@DynamoDbBean
public class BankAccount {

    private String accountId;

    private String accountName;
    private String accountType;

    private BigDecimal balance;

    @DynamoDbPartitionKey
    public String getAccountId() {
        return accountId;
    }
}
