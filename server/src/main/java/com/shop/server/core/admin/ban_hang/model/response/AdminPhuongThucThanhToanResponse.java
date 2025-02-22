package com.shop.server.core.admin.ban_hang.model.response;

import com.shop.server.core.common.base.BaseResponse;

import java.math.BigDecimal;

public interface AdminPhuongThucThanhToanResponse extends BaseResponse {

    String getTenPhuongThuc();

    String getMaGiaoDich ();

    BigDecimal getSoTien ();
}
