package com.shop.server.core.admin.bill.model.response;

import com.shop.server.core.common.base.BaseResponse;

import java.math.BigDecimal;

public interface AdminBillResponse extends BaseResponse {

    String getId();

    BigDecimal getTienGiam();

    BigDecimal getTongTien();

    String getLoaiHD();

    String getTenNguoiNhan();

    String getSoDienThoai();

    String getDiaChiNguoiNhan();

    BigDecimal getTienShip();

    String getNgayShip();

    String getGhiChu();

    String getTrangThai();

    String getMaNhanVien();

    String getTenNhanVien();

    String getMaPhieuGiamGia();

    String getTenPhieuGiamGia();

    String getMaKhachHang();

    String getTenKhachHang();
}
