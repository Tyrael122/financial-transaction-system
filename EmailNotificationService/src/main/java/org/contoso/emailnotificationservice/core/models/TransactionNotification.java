package org.contoso.emailnotificationservice.core.models;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionNotification {
    private String accountId;
    private TransactionType type;

    private BigDecimal amount;
}
