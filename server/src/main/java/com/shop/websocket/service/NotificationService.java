package com.shop.websocket.service;

import com.shop.server.core.common.base.ResponseObject;
import com.shop.server.infrastructure.constants.module.Message;
import com.shop.websocket.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;

    public ResponseObject<?> getAllNotification() {
        return new ResponseObject<>(
                notificationRepository.findAllNotifications(),
                HttpStatus.OK,
                Message.Success.GET_SUCCESS
        );
    }

    public ResponseObject<?> deleteNotification(String id) {
        notificationRepository.deleteById(id);
        return ResponseObject.successForward("Xóa thông báo thành công", Message.Success.DELETE_SUCCESS);
    }

    public ResponseObject<?> deleteAllNotification() {
        notificationRepository.deleteAllNotifications();
        return ResponseObject.successForward("Xóa tất cả thông báo thành công", Message.Success.DELETE_SUCCESS);
    }
}
