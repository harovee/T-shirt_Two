package com.shop.server.infrastructure.email.service;

import com.shop.server.infrastructure.email.entity.EmailDetails;

import java.util.concurrent.CompletableFuture;

public interface EmailService {

    CompletableFuture<String> sendSimpleMail(EmailDetails details);

    CompletableFuture<String> sendMailWithAttachment(EmailDetails details);

}
