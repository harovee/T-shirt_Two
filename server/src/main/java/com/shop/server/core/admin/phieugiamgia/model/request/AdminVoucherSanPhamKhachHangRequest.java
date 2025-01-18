package com.shop.server.core.admin.phieugiamgia.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminVoucherSanPhamKhachHangRequest {
    private PhieuGiamGiaRequest phieuGiamGiaRequest;

    private VoucherSanPhamKhachHangRequest voucherSanPhamKhachHangRequest;
}
