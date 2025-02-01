package com.shop.server.core.admin.phieugiamgia.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdVoucherKhachHangRequest {
    private PhieuGiamGiaRequest phieuGiamGiaRequest;

    private VoucherKhachHangRequest voucherKhachHangRequest;
}
