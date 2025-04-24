package com.shop.server.core.mobile.invoiceconnect;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class ChatControllerTrain {

    @MessageMapping("/send")
    @SendTo("/topic/confirm")
    public Greeting handleMessage(String message) throws Exception {
        System.out.println("Nhận tin nhắn: " + message);
        return new Greeting("Hello, " + message);
    }

    @Setter
    @Getter
    @AllArgsConstructor
    public static class Greeting {
        private String message;
    }

}


