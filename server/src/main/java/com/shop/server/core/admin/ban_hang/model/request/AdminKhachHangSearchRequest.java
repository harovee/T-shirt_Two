package com.shop.server.core.admin.ban_hang.model.request;

import com.shop.server.core.common.base.PageableRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminKhachHangSearchRequest extends PageableRequest {
    private String keyword;
}
