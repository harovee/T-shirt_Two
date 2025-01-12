package com.shop.server.core.admin.sale.models.responses;

import com.shop.server.core.common.base.BaseResponse;

public interface AdminSaleResponse extends BaseResponse {

    String getMaDotGiamGia();

    String getTen();

    String getGiaTri();

    Double getGiaTriGiamToiDa();

    Long getNgayBatDau();

    Long getNgayKetThuc();

    String getTrangThai();


}