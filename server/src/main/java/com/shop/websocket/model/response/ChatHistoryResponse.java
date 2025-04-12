package com.shop.websocket.model.response;

import com.shop.server.core.common.base.BaseResponse;

public interface ChatHistoryResponse extends BaseResponse {
    String getId();

    String getSender();

    Long getCreatedDate();

    String getContent();

    String getRoomId();
}
