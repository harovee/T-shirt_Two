package com.shop.websocket.controller;

import com.shop.websocket.model.entity.ChatMessage;
import com.shop.websocket.service.ChatService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.HashMap;

@Controller
public class ChatController {
    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatController(ChatService chatService, SimpMessagingTemplate messagingTemplate) {
        this.chatService = chatService;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/sendMessage/public")
    public void sendPublicMessage(@Payload ChatMessage message) {
        message.setRoomId("public");
        chatService.saveMessage(message.getSender(), message.getReceiver(), message.getContent(), message.getRoomId());
        messagingTemplate.convertAndSend("/topic/public", message);
        messagingTemplate.convertAndSend("/topic/client-chat-update", "refresh");
    }

    @MessageMapping("/addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage message,
                               SimpMessageHeaderAccessor headerAccessor) {
        if (headerAccessor.getSessionAttributes() == null) {
            headerAccessor.setSessionAttributes(new HashMap<>());
        }
        headerAccessor.getSessionAttributes().put("username", message.getSender());
        return message;
    }

    @MessageMapping("/sendMessage/{roomId}")
    public void sendPrivateMessage(@DestinationVariable String roomId, @Payload ChatMessage message) {
        message.setRoomId(roomId);
        chatService.saveMessage(message.getSender(), message.getReceiver(), message.getContent(), message.getRoomId());
        messagingTemplate.convertAndSend("/topic/private/" + roomId, message);
        messagingTemplate.convertAndSend("/topic/client-chat-update", "refresh");
    }
}
