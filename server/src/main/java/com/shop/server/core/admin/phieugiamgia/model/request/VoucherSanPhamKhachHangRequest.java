package com.shop.server.core.admin.phieugiamgia.model.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class VoucherSanPhamKhachHangRequest {

    private List<String> idSanPham;

    private List<String> idKhachHang;

    private String idVoucher;

    private Integer soLuong;
}
