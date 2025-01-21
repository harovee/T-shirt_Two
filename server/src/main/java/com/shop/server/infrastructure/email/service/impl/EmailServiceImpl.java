package com.shop.server.infrastructure.email.service.impl;

import com.shop.server.infrastructure.email.entity.EmailDetails;
import com.shop.server.infrastructure.email.service.EmailService;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String sender;

    public EmailServiceImpl(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Async
    @Override
    public CompletableFuture<String> sendSimpleMail(EmailDetails details) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            mimeMessage.setFrom(sender);
            mimeMessage.setRecipients(MimeMessage.RecipientType.TO, details.getRecipient());
            mimeMessage.setContent(details.getMsgBody(), "text/html;charset=utf-8");
            mimeMessage.setSubject(details.getSubject());
            javaMailSender.send(mimeMessage);
            Thread.sleep(2000);
            log.info("Mail Sent Successfully To: [{}]", details.getRecipient());
        } catch (Exception e) {
            log.error(e.getMessage(), "Error while Sending Mail");
            e.printStackTrace(System.out);
        }
        return null;
    }

    @Async
    @Override
    public CompletableFuture<String> sendMailWithAttachment(EmailDetails details) {
        MimeMessage mimeMessage
                = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper;

        try {
            mimeMessageHelper
                    = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setFrom(sender);
            mimeMessageHelper.setTo(details.getRecipient());
            mimeMessageHelper.setText(details.getMsgBody());
            mimeMessageHelper.setSubject(
                    details.getSubject());
            FileSystemResource file
                    = new FileSystemResource(
                    new File(details.getAttachment()));

            mimeMessageHelper.addAttachment(
                    file.getFilename(), file);
            javaMailSender.send(mimeMessage);
            Thread.sleep(2000);
            log.info("sendMailWithAttachment + {}", details.getRecipient());
            return CompletableFuture.completedFuture("Mail sent Successfully");
        } catch (Exception e) {
            return CompletableFuture.completedFuture("Error while sending mail!!!");
        }
    }
}
