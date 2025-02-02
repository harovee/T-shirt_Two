package com.shop.server.core.admin.phieugiamgia.model.response;

import com.shop.server.core.common.base.BaseResponse;

import java.time.LocalDate;

public interface PhieuGiamGiaResponse extends BaseResponse {

    String getMa();

    String getTen();

    Short getSoLuong();

    String getDieuKienGiam();

    String getGiamToiDa();

    Boolean getLoaiGiam();

    Double getGiaTriGiam();

    Long getNgayBatDau();

    Long getNgayKetThuc();

    Boolean getKieu();

    String getTrangThai();
}
