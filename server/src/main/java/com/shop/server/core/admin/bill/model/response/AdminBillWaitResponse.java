package com.shop.server.core.admin.bill.model.response;

import com.shop.server.entities.main.KhachHang;
import com.shop.server.entities.main.NhanVien;

import java.math.BigDecimal;

public interface AdminBillWaitResponse {
    String getId();

    String getMa();

    BigDecimal getTongTien();

    BigDecimal getTienGiam();

    BigDecimal getTienShip();

    String getLoaiHD();

    String getTenNguoiNhan();

    String getSoDienThoai();

    String getGhiChu();

    String getTrangThai();

    String getIdNhanVien();

    String getIdKhachHang();

    String getIdPhieuGiamGia ();

    Long getNgayTao();

    String getDiaChiNguoiNhan();
}
