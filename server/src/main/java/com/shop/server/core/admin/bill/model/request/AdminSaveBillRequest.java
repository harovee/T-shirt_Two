package com.shop.server.core.admin.bill.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdminSaveBillRequest {
    private String loaiHD;

    private String idKhachHang;

    private String idNhanVien;

    private String idPhieuGiamGia;

}
