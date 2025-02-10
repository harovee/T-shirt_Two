package com.shop.server.core.admin.ban_hang.model.request;

import com.shop.server.entities.main.KhachHang;
import com.shop.server.entities.main.NhanVien;
import com.shop.server.entities.main.PhieuGiamGia;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class AdminHoaDonRequest {
    private NhanVien nhanVien;

    private KhachHang khachHang;

    private PhieuGiamGia phieuGiamGia;

    private String maHoaDon;

    private BigDecimal tienGiam;

    private BigDecimal tongTien;

    private BigDecimal tienShip;

}
