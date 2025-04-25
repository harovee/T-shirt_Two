package com.shop.websocket.model.response;

import com.shop.server.core.common.base.BaseResponse;

public interface NotificationResponse extends BaseResponse {
    String getId();

    String getOrderId();

    Long getCreatedDate();

    String getContent();

    Boolean getIsRead();

    String getNotificationType();
}
