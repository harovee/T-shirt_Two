package com.shop.websocket.repository;

import com.shop.websocket.model.entity.ChatMessage;
import com.shop.websocket.model.response.ChatHistoryResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, String> {
//    List<ChatMessage> findByRoomIdOrderByCreatedDateAsc(String roomId);

    @Query(value = """
        SELECT
            m.id AS id,
            m.nguoi_gui AS sender,
            m.ngay_tao AS createdDate,
            m.noi_dung AS content,
            m.ma_phong AS roomId
        FROM message m
        WHERE m.ma_phong IS NULL OR m.ma_phong LIKE :#{#roomId}
        ORDER BY createdDate
    """, nativeQuery = true)
    List<ChatHistoryResponse> findByRoomIdOrderByCreatedDateAsc(String roomId);
}
