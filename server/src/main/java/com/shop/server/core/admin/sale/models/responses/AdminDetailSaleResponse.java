package com.shop.server.core.admin.sale.models.responses;

public interface AdminDetailSaleResponse {

    String getId();

    String getMaDotGiamGia();

    String getTen();

    String getLoai();

    Double getGiaTri();

    Double getGiaTriGiamToiDa();

    Long getNgayBatDau();

    Long getNgayKetThuc();

    String getTrangThai();

    Long getCreatedDate();

    Long getLastModifiedDate();

}
