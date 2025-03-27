package org.contoso.checkingaccount.userside;

import org.contoso.checkingaccount.core.TransactionService;
import org.contoso.checkingaccount.core.models.TransactionRequest;
import org.contoso.checkingaccount.core.models.TransactionResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class CheckingAccountController {

    private final TransactionService transactionService;

    public CheckingAccountController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/transaction")
    public TransactionResponse withdraw(@RequestBody TransactionRequest request) {
        return transactionService.handleTransactionRequest(request);
    }
}
