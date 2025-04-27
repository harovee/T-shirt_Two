package com.shop.server.core.admin.bill.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdminUpdateBillWaitRequest {

    private String trangThai;

    private String idKhachHang;

    private String idPhieuGiamGia;

    private String idNhanVien;

    private String diaChiNguoiNhan;

    private String tenNguoiNhan;

    private String soDienThoai;

    private Long ngayShip;

    private String ghiChu;

    private BigDecimal tienGiam;

    private BigDecimal tienShip;

    private BigDecimal tongTien;

    private String tinh;

    private String huyen;

    private String xa;

    private String phuongThucNhan;
}
