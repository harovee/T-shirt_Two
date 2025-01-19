package com.shop.server.core.admin.phieugiamgia.model.request;

import com.shop.server.core.common.base.PageableRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminKhachHangSearchRequest  extends PageableRequest {
    String keyword;
}
