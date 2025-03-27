package org.contoso.checkingaccount.core;

import lombok.extern.slf4j.Slf4j;
import org.contoso.checkingaccount.core.models.*;
import org.contoso.checkingaccount.core.ports.BankAccountRepository;
import org.contoso.checkingaccount.core.ports.TransactionListener;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class TransactionService {
    private final BankAccountRepository bankAccountRepository;
    private final TransactionListener transactionListener;

    public TransactionService(BankAccountRepository bankAccountRepository, TransactionListener transactionListener) {
        this.bankAccountRepository = bankAccountRepository;
        this.transactionListener = transactionListener;
    }

    public TransactionResponse handleTransactionRequest(TransactionRequest transactionRequest) {
        Optional<BankAccount> optionalAccount = bankAccountRepository.getById(transactionRequest.getAccountId());
        if (optionalAccount.isEmpty()) {
            log.error("Account with id {} not found", transactionRequest.getAccountId());
            throw new RuntimeException("Account with id " + transactionRequest.getAccountId() + " not found");
        }

        BankAccount account = optionalAccount.get();

        handleTransactionBasedOnType(transactionRequest, account);

        account = bankAccountRepository.save(account);

        notifyListeners(transactionRequest);

        return new TransactionResponse(account.getBalance());
    }

    private void notifyListeners(TransactionRequest transactionRequest) {
        TransactionNotification transactionNotification = new TransactionNotification();

        transactionNotification.setAccountId(transactionRequest.getAccountId());
        transactionNotification.setAmount(transactionRequest.getAmount());
        transactionNotification.setType(transactionRequest.getType());

        transactionListener.notifyTransaction(transactionNotification);
    }

    private void handleTransactionBasedOnType(TransactionRequest transactionRequest, BankAccount account) {
        if (transactionRequest.getType() == TransactionType.DEPOSIT) {
            account.setBalance(account.getBalance().add(transactionRequest.getAmount()));
        } else if (transactionRequest.getType() == TransactionType.WITHDRAWAL) {
            account.setBalance(account.getBalance().subtract(transactionRequest.getAmount()));
        } else {
            throw new RuntimeException("Transaction type " + transactionRequest.getType() + " not supported");
        }
    }
}
