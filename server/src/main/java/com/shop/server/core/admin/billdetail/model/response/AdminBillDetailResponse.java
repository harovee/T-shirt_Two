package com.shop.server.core.admin.billdetail.model.response;

import com.shop.server.core.common.base.BaseResponse;

import java.math.BigDecimal;

public interface AdminBillDetailResponse extends BaseResponse {
    String getId();

    String getIdHoaDon();

    String getMaHoaDon();

    String getIdSanPhamChiTiet();

    String getTenSanPhamChiTiet();

    String getTenSanPham();

    String getImgUrl();

    String getTenKichCo();

    String getTenMau();

    Short getSoLuong();

    BigDecimal getGia();

    BigDecimal getThanhTien();

    BigDecimal getTongTienHD();

    BigDecimal getTienGiamHD();

    BigDecimal getTienShip();

    Boolean getLoaiGiam(); // 1 tiền // 0 %

    String getGiamToiDa();

    Double getGiaTriGiam();

    String getDieuKienGiam();

    String getTenPhieuGiam();

}
