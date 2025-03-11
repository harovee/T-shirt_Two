package com.shop.server.core.admin.ban_hang.model.request;

import com.shop.server.core.common.base.PageableRequest;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class AdminVoucherRequest{
    String keyword;

    String idKhachHang;
}
