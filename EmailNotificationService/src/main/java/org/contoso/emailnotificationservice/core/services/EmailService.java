package org.contoso.emailnotificationservice.core.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.contoso.emailnotificationservice.core.models.EmailRequest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender emailSender;

    public EmailService(JavaMailSender javaMailSender) {
        this.emailSender = javaMailSender;
    }

    public void sendEmail(EmailRequest emailRequest) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(emailRequest.getTo());
        message.setSubject(emailRequest.getSubject());
        message.setText(emailRequest.getBody());

        emailSender.send(message);
    }

    public void sendHtmlEmail(EmailRequest emailRequest) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(emailRequest.getTo());
        helper.setSubject(emailRequest.getSubject());
        helper.setText(emailRequest.getBody(), true); // true indicates HTML

        message.setContent(emailRequest.getBody(), "text/html; charset=UTF-8");

        emailSender.send(message);
    }
}