package com.shop.server.core.admin.ban_hang.service;

import com.shop.server.core.admin.ban_hang.model.request.*;
import com.shop.server.core.common.base.ResponseObject;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public interface AdminPaymentServices {
    ResponseObject<?> getAllKhachHang(AdminKhachHangSearchRequest request);

    ResponseObject<?> getKhachHangById(String idKhachHang);

//    ResponseObject<?> getAllVoucherKhachHang(AdminHoaDonKhachHangRequest request, String idKhachHang);

    ResponseObject<?> getAllVoucherKhachHangNoId(AdminHoaDonKhachHangRequest request);

    ResponseObject<?> getVoucherKhachHangById(AdminHoaDonKhachHangRequest request);

    ResponseObject<?> getPhuongThucThanhToan(AdminPaymentMethodDetailRequest request);

    ResponseObject<?> savePayBill(AdminHoaDonKhachHangRequest request);

    ResponseObject<?> getCustomerAddressByIdCustomer(AdminCustomerAddressSearchRequest request);

    ResponseObject<?> getNextTotalPriceToVoucher (AdminHoaDonKhachHangRequest request);

    ResponseObject<?> addPaymentMethodDetail (AdminPaymentMethodDetailRequest request);

    ResponseObject<?> getCustomerByPhoneNumber (String phoneNumber);

    ResponseObject<?> getWardByCode(String code);

    ResponseObject<?> getDistrictById(String id);

    ResponseObject<?> getProvinceById(String id);

    ResponseObject<?> getVoucherByCode(AdminVoucherRequest request);
}
