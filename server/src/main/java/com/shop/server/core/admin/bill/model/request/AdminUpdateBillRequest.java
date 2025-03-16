package com.shop.server.core.admin.bill.model.request;

import com.shop.server.core.common.base.PageableRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdminUpdateBillRequest {
    private String trangThai;

    private String idKhachHang;

    private String idPhieuGiamGia;

    private String diaChiNguoiNhan;

    private String tenNguoiNhan;

    private String soDienThoai;

    private String ghiChu;

    //lich su hoa don
    private String idHoaDon;

    private String hanhDong;

    private String moTa;

    private Long ngayTao;

    private String tinh;

    private String huyen;

    private BigDecimal tienShip;

    private BigDecimal tienGiam;

    private String xa;

    private BigDecimal tongTien;
}
