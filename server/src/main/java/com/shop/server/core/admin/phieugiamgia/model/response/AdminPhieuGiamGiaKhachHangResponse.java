package com.shop.server.core.admin.phieugiamgia.model.response;

import com.shop.server.core.common.base.BaseResponse;

public interface AdminPhieuGiamGiaKhachHangResponse extends BaseResponse {
    String getIdSanPhamChiTiet();

    String getIdKhachHang();

    String getIdVoucher();

    Integer getSoLuong();
}
