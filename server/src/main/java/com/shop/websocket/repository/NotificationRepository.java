package com.shop.websocket.repository;

import com.shop.websocket.model.entity.OrderNotification;
import com.shop.websocket.model.response.NotificationResponse;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<OrderNotification, String> {
    @Query(value = """
        SELECT
            noti.id AS id,
            noti.ma_hoa_don AS orderId,
            noti.ngay_tao AS createdDate,
            noti.noi_dung AS content,
            noti.da_xem AS isRead
        FROM notification noti
        WHERE noti.da_xem = false
        ORDER BY createdDate DESC
    """, nativeQuery = true)
    List<NotificationResponse> findAllNotifications();

    @Transactional
    @Modifying
    @Query("""
        DELETE FROM OrderNotification
        """)
    void deleteAllNotifications();
}
