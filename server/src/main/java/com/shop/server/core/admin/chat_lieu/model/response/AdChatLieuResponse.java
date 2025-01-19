package com.shop.server.core.admin.chat_lieu.model.response;

import com.shop.server.core.common.base.BaseResponse;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;
public interface AdChatLieuResponse extends BaseResponse {

    String getTen ();

    String getMaChatLieu ();

    Long getNgayTao ();
}
