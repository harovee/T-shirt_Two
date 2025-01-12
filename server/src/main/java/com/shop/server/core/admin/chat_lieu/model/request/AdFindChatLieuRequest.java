package com.shop.server.core.admin.chat_lieu.model.request;

import com.shop.server.core.common.base.PageableRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdFindChatLieuRequest extends PageableRequest {

    private String keyword;
}
