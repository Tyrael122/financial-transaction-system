package org.contoso.checkingaccount.core.ports;

import org.contoso.checkingaccount.core.models.TransactionNotification;

public interface TransactionListener {
    void notifyTransaction(TransactionNotification transactionNotification);
}
