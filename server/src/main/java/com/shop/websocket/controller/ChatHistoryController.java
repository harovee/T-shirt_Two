package com.shop.websocket.controller;

import com.shop.server.infrastructure.constants.module.MappingConstant;
import com.shop.websocket.model.response.ChatHistoryResponse;
import com.shop.websocket.service.ChatService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(MappingConstant.API_ADMIN_CHAT_HISTORY)
@CrossOrigin(origins = "*")
public class ChatHistoryController {
    private final ChatService chatService;

    public ChatHistoryController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/messages/{roomId}")
    public List<ChatHistoryResponse> getMessages(@PathVariable String roomId) {
        return chatService.getMessageByRoomId(roomId);
    }

//    @PutMapping("/messages/{roomId}/read")
//    public void markMessagesAsRead(@PathVariable String roomId) {
//        chatService.markMessagesAsRead(roomId);
//    }
}
