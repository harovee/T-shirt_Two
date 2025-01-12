package com.shop.server.core.admin.chat_lieu.model.response;

import com.shop.server.core.common.base.BaseResponse;

import java.time.LocalDate;

public interface AdChatLieuResponse extends BaseResponse {

    String getTen ();

    String getMaChatLieu ();

    Long getNgayTao ();
}
