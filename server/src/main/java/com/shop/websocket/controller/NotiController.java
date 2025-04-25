package com.shop.websocket.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class NotiController {

    @MessageMapping("/sendNoti")
    @SendTo("/topic/notification")
    public String sendNortification(String message) {
        return message;
    }
}
