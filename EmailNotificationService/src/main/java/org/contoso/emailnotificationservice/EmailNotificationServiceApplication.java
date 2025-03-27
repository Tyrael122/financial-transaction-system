package org.contoso.emailnotificationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.function.Function;

@SpringBootApplication
public class EmailNotificationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmailNotificationServiceApplication.class, args);
    }
}
