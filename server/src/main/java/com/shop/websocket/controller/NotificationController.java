package com.shop.websocket.controller;

import com.shop.server.infrastructure.constants.module.MappingConstant;
import com.shop.server.utils.Helper;
import com.shop.websocket.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(MappingConstant.API_ADMIN_ORDER_NOTIFICATION)
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping()
    ResponseEntity<?> getAllNotifications() {
        return Helper.createResponseEntity(notificationService.getAllNotification());
    }

    @DeleteMapping("/{id}")
    ResponseEntity<?> deleteNotifications(@PathVariable String id) {
        return Helper.createResponseEntity(notificationService.deleteNotification(id));
    }

    @DeleteMapping("/all-notification")
    ResponseEntity<?> deleteAllNotifications() {
        return Helper.createResponseEntity(notificationService.deleteAllNotification());
    }
}
