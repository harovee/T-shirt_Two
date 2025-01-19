package com.shop.server.core.admin.phieugiamgia.model.response;

import com.shop.server.core.common.base.BaseResponse;

import java.math.BigDecimal;

public interface AdminProductDetailResponse extends BaseResponse {
    String getTen();

    String getMaSPCT();

    Integer getSoLuong();

    BigDecimal getGia();

    String getTenDanhMuc();

    String getTenChatLieu();

    String getTenThuongHieu();
}
