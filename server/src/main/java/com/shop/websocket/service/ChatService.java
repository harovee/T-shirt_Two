package com.shop.websocket.service;

import com.shop.websocket.model.entity.ChatMessage;
import com.shop.websocket.model.response.ChatHistoryResponse;
import com.shop.websocket.repository.ChatMessageRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatService {
    public final ChatMessageRepository chatMessageRepository;

    public ChatService(ChatMessageRepository chatMessageRepository) {
        this.chatMessageRepository = chatMessageRepository;
    }

    public ChatMessage saveMessage(String sender, String receiver, String content, String roomId) {
        ChatMessage message = new ChatMessage();
        message.setType(ChatMessage.MessageType.CHAT);
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setRoomId(roomId);
        message.setContent(content);
        return chatMessageRepository.save(message);
    }


    public List<ChatHistoryResponse> getMessageByRoomId(String roomId) {
        return chatMessageRepository.findByRoomIdOrderByCreatedDateAsc(roomId);
    }

//    public void markMessagesAsRead(String roomId) {
//        chatMessageRepository.markMessagesAsReadByRoomId(roomId);
//    }
}
