package com.shop.server.core.admin.ban_hang.service;

import com.shop.server.core.admin.ban_hang.model.request.AdminHoaDonKhachHangRequest;
import com.shop.server.core.admin.ban_hang.model.request.AdminKhachHangSearchRequest;
import com.shop.server.core.admin.ban_hang.model.request.AdminVoucherRequest;
import com.shop.server.core.common.base.ResponseObject;
import org.springframework.stereotype.Service;

@Service
public interface AdminPaymentServices {
    ResponseObject<?> getAllKhachHang(AdminKhachHangSearchRequest request);

    ResponseObject<?> getKhachHangById(String idKhachHang);

//    ResponseObject<?> getAllVoucherKhachHang(AdminHoaDonKhachHangRequest request, String idKhachHang);

    ResponseObject<?> getAllVoucherKhachHangNoId(AdminHoaDonKhachHangRequest request);

    ResponseObject<?> getVoucherKhachHangById(AdminHoaDonKhachHangRequest request);

    ResponseObject<?> getPhuongThucThanhToan();

    ResponseObject<?> savePayBill(AdminHoaDonKhachHangRequest request);


}
