package org.contoso.checkingaccount.core.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class TransactionResponse {
    private BigDecimal updatedBalance;
}