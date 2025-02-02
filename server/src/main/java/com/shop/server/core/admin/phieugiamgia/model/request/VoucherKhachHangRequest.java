package com.shop.server.core.admin.phieugiamgia.model.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class VoucherKhachHangRequest {

    private List<String> idKhachHangs;

    private String idPhieuGiamGia;

    private String nhanVien;
}
