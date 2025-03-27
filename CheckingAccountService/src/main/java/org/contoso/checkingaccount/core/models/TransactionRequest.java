package org.contoso.checkingaccount.core.models;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionRequest {
    private String accountId;
    private BigDecimal amount;
    private String currency;
    private String description;

    private TransactionType type;
}
