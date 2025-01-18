package com.shop.server.core.admin.phieugiamgia.model.response;

import com.shop.server.core.common.base.BaseResponse;

public interface AdminVoucherSanPhamKhachHangResponse extends BaseResponse {
    String getIdSanPhamChiTiet();

    String getIdKhachHang();

    String getIdVoucher();

    Integer getSoLuong();
}
