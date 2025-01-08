package com.shop.server.core.admin.billdetail.model.response;

import com.shop.server.core.common.base.BaseResponse;

import java.math.BigDecimal;

public interface AdminBillDetailResponse extends BaseResponse {
    String getId();

    String getIdHoaDon();

    String getIdSanPhamChiTiet();

    String getTenSanPhamChiTiet();

    Short getSoLuong();

    BigDecimal getGia();

    BigDecimal getThanhTien();

    String getTrangThai();
}
