package org.contoso.emailnotificationservice.core.models;

import lombok.Data;

@Data
public class EmailRequest {

    private String to;
    private String subject;
    private String body;

}
