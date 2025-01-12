package com.shop.server.core.admin.billdetail.model.response;

import com.shop.server.core.common.base.BaseResponse;

import java.math.BigDecimal;

public interface AdminBillDetailResponse extends BaseResponse {
    String getId();

    String getIdHoaDon();

    String getMaHoaDon();

    String getIdSanPhamChiTiet();

    String getTenSanPhamChiTiet();

    Short getSoLuong();

    BigDecimal getGia();

    BigDecimal getThanhTien();

    String getTrangThaiHD();

    BigDecimal getTongTienHD();

    BigDecimal getTienGiamHD();

    String getLoaiHoaDon();

    String getTenNguoiNhan();

    String getSoDienThoai();

    String getDiaChiNguoiNhan();

    BigDecimal getTienShip();

    String getNgayShip();

    String getGhiChuHD();

    String getTenNhanVien();

    String getTenKhachHang();
}
