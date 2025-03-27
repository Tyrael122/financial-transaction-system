package org.contoso.checkingaccount.serverside;

import io.awspring.cloud.dynamodb.DynamoDbTemplate;
import lombok.extern.slf4j.Slf4j;
import org.contoso.DynamoDbTemplateBuilder;
import org.contoso.checkingaccount.core.models.BankAccount;
import org.contoso.checkingaccount.core.ports.BankAccountRepository;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.enhanced.dynamodb.Key;

import java.util.Optional;

@Slf4j
@Repository
public class BankAccountRepositoryDynamoDB implements BankAccountRepository {

    private final DynamoDbTemplate dynamoDbTemplate = DynamoDbTemplateBuilder.builder().withTableName("bank_account").build();

    @Override
    public Optional<BankAccount> getById(String accountId) {
        BankAccount account = null;
        try {
            account = dynamoDbTemplate.load(Key.builder().partitionValue(accountId).build(), BankAccount.class);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return Optional.ofNullable(account);
    }

    @Override
    public BankAccount save(BankAccount account) {
        return dynamoDbTemplate.save(account);
    }
}
