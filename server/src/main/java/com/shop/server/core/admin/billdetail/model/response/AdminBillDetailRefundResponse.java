package com.shop.server.core.admin.billdetail.model.response;

import com.shop.server.core.common.base.BaseResponse;

import java.math.BigDecimal;

public interface AdminBillDetailRefundResponse extends BaseResponse {
    String getId();

    String getTenSanPham();

    String getAnhSanPhamChiTiet();

    String getTenKC();

    String getTenMau();

    Short getSoLuong();

    BigDecimal getGia();

    BigDecimal getThanhTien();

}
