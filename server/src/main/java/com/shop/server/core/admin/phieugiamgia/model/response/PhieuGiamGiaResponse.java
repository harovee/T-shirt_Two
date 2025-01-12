package com.shop.server.core.admin.phieugiamgia.model.response;

import java.time.LocalDate;

public interface PhieuGiamGiaResponse {
    String getId();

    String getMa();

    String getTen();

    Short getSoLuong();

    String getDieuKienGiam();

    String getGiamToiDa();

    Boolean getLoaiGiam();

    Double getGiaTriGiam();

    LocalDate getNgayBatDau();

    LocalDate getNgayKetThuc();

    String getTrangThai();
}
