package com.shop.server.infrastructure.email.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmailDetails {

    private String recipient;

    private String msgBody;

    private String subject;

    private String attachment;

}
