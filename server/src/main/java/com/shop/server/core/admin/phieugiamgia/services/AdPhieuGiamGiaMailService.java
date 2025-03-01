package com.shop.server.core.admin.phieugiamgia.services;

import com.shop.server.entities.main.KhachHang;
import com.shop.server.entities.main.PhieuGiamGia;

public interface AdPhieuGiamGiaMailService {
    void sendMailCreateKhachHangVoucher(KhachHang khachHang, PhieuGiamGia phieuGiamGia);

    void sendMailUpdateKhachHangVoucher(KhachHang khachHang, PhieuGiamGia phieuGiamGia);

    void sendMailCancelKhachHangVoucher(KhachHang khachHang, PhieuGiamGia phieuGiamGia);
}
