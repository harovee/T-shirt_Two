package com.shop.server.core.admin.product.models.responses;

import com.shop.server.core.common.base.BaseResponse;

public interface AdminProductResponse extends BaseResponse {

    String getMaSanPham();

    String getTen();

    Long getNgayTao();

    Long getSoLuong();

    Integer getTrangThai();

    String getTenDanhMuc();

}