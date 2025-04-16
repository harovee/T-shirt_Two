package com.shop.websocket.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class NortiService {
    @Autowired
    private SimpMessagingTemplate template;

    public void sendNoti(String message) {
        template.convertAndSend("/topic/notification", message);
    }
}
