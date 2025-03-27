package org.contoso.checkingaccount.core.models;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionNotification {
    private String accountId;
    private BigDecimal amount;

    private TransactionType type;
}
