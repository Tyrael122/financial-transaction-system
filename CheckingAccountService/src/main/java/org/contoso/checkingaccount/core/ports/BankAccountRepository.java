package org.contoso.checkingaccount.core.ports;

import org.contoso.checkingaccount.core.models.BankAccount;

import java.util.Optional;

public interface BankAccountRepository {
    Optional<BankAccount> getById(String accountId);

    BankAccount save(BankAccount account);
}
