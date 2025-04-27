package com.shop.websocket.controller;

import com.shop.websocket.model.entity.OrderNotification;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class NotiSocketController {

    @MessageMapping("/sendNoti")
    @SendTo("/topic/notification")
    public OrderNotification sendNortification(OrderNotification notification) {
        return notification;
    }
}
