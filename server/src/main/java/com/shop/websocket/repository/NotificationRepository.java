package com.shop.websocket.repository;

import com.shop.websocket.model.entity.OrderNotification;
import com.shop.websocket.model.response.NotificationResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<OrderNotification, String> {
    @Query(value = """
        SELECT
            noti.id AS id,
            noti.ma_hoa_don AS orderId,
            noti.nguoi_tao AS createdDate,
            noti.noi_dung AS content,
            noti.da_xem AS isRead,
            noti.loai_thong_bao AS notificationType
        FROM notification noti
        ORDER BY createdDate DESC
    """, nativeQuery = true)
    List<NotificationResponse> findAllNotifications(String orderId);
}
