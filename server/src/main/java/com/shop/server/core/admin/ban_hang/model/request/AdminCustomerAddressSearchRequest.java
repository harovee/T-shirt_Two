package com.shop.server.core.admin.ban_hang.model.request;

import com.shop.server.core.common.base.PageableRequest;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AdminCustomerAddressSearchRequest extends PageableRequest {
    String keyword;

    String idKhachHang;
}
