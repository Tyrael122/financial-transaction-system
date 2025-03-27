package org.contoso.emailnotificationservice.core.services;

import jakarta.mail.MessagingException;
import org.contoso.emailnotificationservice.core.models.EmailRequest;
import org.contoso.emailnotificationservice.core.models.TransactionNotification;
import org.contoso.emailnotificationservice.core.models.TransactionType;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class TransactionNotificationService {
    private final EmailService emailService;
    private final TemplateEngine templateEngine;

    public TransactionNotificationService(EmailService emailService, TemplateEngine templateEngine) {
        this.emailService = emailService;
        this.templateEngine = templateEngine;
    }

    public void handleNotification(TransactionNotification transactionNotification) {
        String accountOwnerEmail = retrieveAccountOwnerEmail(transactionNotification.getAccountId());
        sendEmailToAccountOwner(transactionNotification, accountOwnerEmail);
    }

    private String retrieveAccountOwnerEmail(String accountId) {
        return "borges.kauan.martins@gmail.com";
    }

    private void sendEmailToAccountOwner(TransactionNotification transactionNotification, String accountOwnerEmail) {
        EmailRequest emailToSend = buildEmailRequest(transactionNotification);
        emailToSend.setTo(accountOwnerEmail);

        try {
            emailService.sendHtmlEmail(emailToSend);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    private EmailRequest buildEmailRequest(TransactionNotification transactionNotification) {
        EmailRequest emailRequest = new EmailRequest();

        var transactionType = transactionNotification.getType();

        if (transactionType == TransactionType.DEPOSIT) {
            emailRequest.setSubject("Pix recebido");
        } else if (transactionType == TransactionType.WITHDRAWAL) {
            emailRequest.setSubject("Pix realizado!");
        }

        String htmlContent = buildHtmlContent(transactionNotification);
        emailRequest.setBody(htmlContent);

        return emailRequest;
    }

    private String buildHtmlContent(TransactionNotification transactionNotification) {
        Context context = new Context();
        context.setVariable("recipientName", "Leslie");
        context.setVariable("destinationName", "Bob Charlie");
        context.setVariable("amount", transactionNotification.getAmount());
        context.setVariable("formattedDate", DateTimeFormatter.ofPattern("hh:mm dd/MM/yyyy").format(LocalDateTime.now()));

        return templateEngine.process("pix-notification", context);
    }
}