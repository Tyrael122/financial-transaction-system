package org.contoso.checkingaccount.serverside;

import io.awspring.cloud.sns.core.SnsTemplate;
import org.contoso.checkingaccount.core.models.TransactionNotification;
import org.contoso.checkingaccount.core.ports.TransactionListener;
import org.springframework.stereotype.Service;

@Service
public class TransactionListenerSnsTopic implements TransactionListener {
    private final SnsTemplate snsTemplate;

    public TransactionListenerSnsTopic(SnsTemplate snsTemplate) {
        this.snsTemplate = snsTemplate;
    }

    @Override
    public void notifyTransaction(TransactionNotification transactionNotification) {
        snsTemplate.sendNotification("transactionstopic", transactionNotification, "dudejustwork");
    }
}
