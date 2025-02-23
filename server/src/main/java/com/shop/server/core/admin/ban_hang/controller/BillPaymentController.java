package com.shop.server.core.admin.ban_hang.controller;

import com.shop.server.core.admin.ban_hang.model.request.*;
import com.shop.server.core.admin.ban_hang.service.AdminPaymentServices;
import com.shop.server.infrastructure.constants.module.MappingConstant;
import com.shop.server.utils.Helper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequiredArgsConstructor
@RequestMapping(MappingConstant.API_ADMIN_PAYMENT)
@CrossOrigin("*")
public class BillPaymentController {

    private final AdminPaymentServices adminPaymentServices;

    @GetMapping("/khach-hang")
    public ResponseEntity<?> getListkhachHang(@Valid final AdminKhachHangSearchRequest request) {
        return Helper.createResponseEntity(
                    adminPaymentServices.getAllKhachHang(request));
    }

    @GetMapping("/khach-hang/{id}")
    public ResponseEntity<?> getKhachHangById(@PathVariable String id) {
        return Helper.createResponseEntity(
                    adminPaymentServices.getKhachHangById(id));
    }

//    @GetMapping("/voucher/all/{id}")
//    public ResponseEntity<?> getVoucherInKhachHang(@Valid final AdminHoaDonKhachHangRequest request, @PathVariable ("id") String id) {
//        return Helper.createResponseEntity(adminPaymentServices.getAllVoucherKhachHang(request, id));
//    }

    @GetMapping("/voucher")
    public ResponseEntity<?> getVoucherInKhachHangAll(@Valid final AdminHoaDonKhachHangRequest request) {
        return Helper.createResponseEntity(adminPaymentServices.getAllVoucherKhachHangNoId(request));
    }

    @GetMapping("/voucher/next-voucher")
    public ResponseEntity<?> getNextVoucherByTotalPrice(@Valid final AdminHoaDonKhachHangRequest request) {
        return Helper.createResponseEntity(adminPaymentServices.getNextTotalPriceToVoucher(request));
    }

//    @GetMapping("/voucher/{id}")
//    public ResponseEntity<?> getVoucherInKhachHangById(@PathVariable String id) {
//        return Helper.createResponseEntity(null);
//    }

    @GetMapping("/payment-method-detail")
    public ResponseEntity<?> getPaymentMethod(@Valid final AdminPaymentMethodDetailRequest request) {
        return Helper.createResponseEntity(adminPaymentServices.getPhuongThucThanhToan(request));
    }

    @PostMapping("/payment-method-detail")
    public ResponseEntity<?> addPaymentMethodDetail(@RequestBody AdminPaymentMethodDetailRequest request) {
        return Helper.createResponseEntity(adminPaymentServices.addPaymentMethodDetail(request));
    }

    @GetMapping("/customer-address")
    public ResponseEntity<?> getCustomerAddressById(final AdminCustomerAddressSearchRequest request) {
        return Helper.createResponseEntity(adminPaymentServices.getCustomerAddressByIdCustomer(request));
    }

    @GetMapping("/customer/{phoneNumber}")
    public ResponseEntity<?> getCustomerByPhoneNumber(@PathVariable ("phoneNumber") String phoneNumber) {
        return Helper.createResponseEntity(adminPaymentServices.getCustomerByPhoneNumber(phoneNumber));
    }

    @GetMapping("/ward/{code}")
    public ResponseEntity<?> getWardByCode(@PathVariable ("code") String code) {
        return Helper.createResponseEntity(adminPaymentServices.getWardByCode(code));
    }

    @GetMapping("/district/{id}")
    public ResponseEntity<?> getDistrictByCode(@PathVariable ("id") String id) {
        return Helper.createResponseEntity(adminPaymentServices.getDistrictById(id));
    }

    @GetMapping("/province/{id}")
    public ResponseEntity<?> getProvinceByCode(@PathVariable ("id") String id) {
        return Helper.createResponseEntity(adminPaymentServices.getProvinceById(id));
    }
}
