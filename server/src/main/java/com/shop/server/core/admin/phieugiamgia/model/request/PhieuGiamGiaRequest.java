package com.shop.server.core.admin.phieugiamgia.model.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class PhieuGiamGiaRequest {
    private String ma;

    private String ten;

    private Short soLuong;

    private String dieuKienGiam;

    private String giamToiDa;

    private Boolean loaiGiam;

    private Double giaTriGiam;

    private Boolean kieu;

    private Long ngayBatDau;

    private Long ngayKetThuc;

    private String trangThai = "ACTIVE";
}
