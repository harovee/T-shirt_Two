package com.shop.server.core.admin.phieugiamgia.services;

import com.shop.server.core.admin.phieugiamgia.model.request.AdminKhachHangSearchRequest;
import com.shop.server.core.admin.phieugiamgia.model.request.AdminProductSearchRequest;
import com.shop.server.core.admin.phieugiamgia.model.request.AdminVoucherSanPhamKhachHangRequest;
import com.shop.server.core.admin.phieugiamgia.model.request.PhieuGiamGiaRequest;
import com.shop.server.core.admin.phieugiamgia.model.request.PhieuGiamGiaSearchRequest;
import com.shop.server.core.admin.phieugiamgia.model.request.VoucherSanPhamKhachHangRequest;
import com.shop.server.core.common.base.ResponseObject;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface AdPhieuGiamGiaServices {

    public ResponseObject<?> getAllPhieuGiamGia(PhieuGiamGiaSearchRequest request);

    public ResponseObject<?> getPhieuGiamGiaById(String id);

    public ResponseObject<?> createPhieuGiamGia(PhieuGiamGiaRequest request);

    public  ResponseObject<?> updatePhieuGiamGiaById(PhieuGiamGiaRequest request,String id);

    public ResponseObject<?> deletePhieuGiamGiaById(String id);

    public ResponseObject<?> updateTrangThai();

    public ResponseObject<?> getAllProductDetail(AdminProductSearchRequest request);

    public ResponseObject<?> getProductDetailById(String id);

    public  ResponseObject<?> getAllKhachHang(AdminKhachHangSearchRequest request);

    public ResponseObject<?> getKhachHangById(String id);

    public ResponseObject<?> createVoucherSanphamKhachHang(PhieuGiamGiaRequest voucher, VoucherSanPhamKhachHangRequest voucherSanPhamKhachHang);
}
