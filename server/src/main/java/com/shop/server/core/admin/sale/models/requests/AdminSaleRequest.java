package com.shop.server.core.admin.sale.models.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdminSaleRequest {

    private String ma;

    private String ten;

    private String loai; // PERCENT, VND

    private Double giaTri;

    private Double giaTriGiamToiDa;

    private Long ngayBatDau;

    private Long ngayKetThuc;

    private String nguoiSua;

    private String trangThai = "ACTIVE"; // ACTIVE, INACTIVE

}
