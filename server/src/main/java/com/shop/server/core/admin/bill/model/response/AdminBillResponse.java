package com.shop.server.core.admin.bill.model.response;

import com.shop.server.core.common.base.BaseResponse;

import java.math.BigDecimal;

public interface AdminBillResponse extends BaseResponse {

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

    String getMaNhanVien();

    Long getNgayTao();

    String getTenKhachHang();

    String getDiaChiNguoiNhan();

    String getPhuongthucNhan();

    String getIdPhieuGiamGia();

    String getTinh();

    String getHuyen();

    String getXa();
}
