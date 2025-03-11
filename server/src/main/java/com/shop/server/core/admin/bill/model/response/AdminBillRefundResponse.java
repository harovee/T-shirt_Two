package com.shop.server.core.admin.bill.model.response;

import com.shop.server.core.common.base.BaseResponse;

import java.math.BigDecimal;

public interface AdminBillRefundResponse extends BaseResponse {

    String getId();

    String getMa();

    BigDecimal getTongTien();

    BigDecimal getTienGiam();

    String getTenNguoiNhan();

    String getSoDienThoai();

    String getTenKhachHang();

    String getDiaChiNguoiNhan();
}
