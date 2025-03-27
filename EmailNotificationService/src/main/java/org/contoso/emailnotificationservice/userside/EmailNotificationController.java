package org.contoso.emailnotificationservice.userside;

import com.amazonaws.services.lambda.runtime.events.SNSEvent;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.StreamReadFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import lombok.extern.slf4j.Slf4j;
import org.contoso.emailnotificationservice.core.models.TransactionNotification;
import org.contoso.emailnotificationservice.core.services.TransactionNotificationService;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Slf4j
@Service
public class EmailNotificationController {

    private final ObjectMapper objectMapper = JsonMapper.builder()
            .addModule(new JodaModule())
            .configure(StreamReadFeature.INCLUDE_SOURCE_IN_LOCATION, true)
            .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
            .build();

    private final TransactionNotificationService transactionNotificationService;

    public EmailNotificationController(TransactionNotificationService transactionNotificationService) {
        this.transactionNotificationService = transactionNotificationService;
    }

    @Bean
    public Consumer<SQSEvent> handleNotification() {
        return event -> {
            String body = event.getRecords().getFirst().getBody();
            try {
                SNSEvent.SNS snsMessage = objectMapper.readValue(body, SNSEvent.SNS.class);

                log.info(snsMessage.toString());

                var notification = objectMapper.readValue(snsMessage.getMessage(), TransactionNotification.class);

                log.info(notification.toString());

                transactionNotificationService.handleNotification(notification);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        };
    }
}
