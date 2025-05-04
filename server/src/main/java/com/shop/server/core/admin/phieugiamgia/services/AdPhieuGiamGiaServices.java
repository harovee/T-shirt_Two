package com.shop.server.core.admin.phieugiamgia.services;

import com.shop.server.core.admin.phieugiamgia.model.request.AdVoucherKhachHangRequest;
import com.shop.server.core.admin.phieugiamgia.model.request.AdminKhachHangSearchRequest;
import com.shop.server.core.admin.phieugiamgia.model.request.PhieuGiamGiaRequest;
import com.shop.server.core.admin.phieugiamgia.model.request.PhieuGiamGiaSearchRequest;
import com.shop.server.core.admin.phieugiamgia.model.request.VoucherKhachHangRequest;
import com.shop.server.core.common.base.ResponseObject;
import org.springframework.stereotype.Service;

@Service
public interface AdPhieuGiamGiaServices {

    public ResponseObject<?> getAllPhieuGiamGia(PhieuGiamGiaSearchRequest request);

    public ResponseObject<?> getPhieuGiamGiaById(String id);

    public ResponseObject<?> createPhieuGiamGia(PhieuGiamGiaRequest request);

    public  ResponseObject<?> updatePhieuGiamGiaById(PhieuGiamGiaRequest request,String id);

    public ResponseObject<?> deletePhieuGiamGiaById(String id);

    public  ResponseObject<?> getAllKhachHang(AdminKhachHangSearchRequest request);

    public ResponseObject<?> createVoucherKhachHang(PhieuGiamGiaRequest voucher, VoucherKhachHangRequest voucherSanPhamKhachHang);

    public ResponseObject<?> getKhachHangByIdPhieuGiamGia(String idPhieuGiamGia);

    public ResponseObject<?> updateVoucherKhachHang(String id, AdVoucherKhachHangRequest request);

    public ResponseObject<?> changeStatusPhieuGiamGia(String id,String trangThai);

    public ResponseObject<?> checkVoucherInUse(String id);
}
