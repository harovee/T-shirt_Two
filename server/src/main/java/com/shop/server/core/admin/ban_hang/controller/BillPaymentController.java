package com.shop.server.core.admin.ban_hang.controller;

import com.shop.server.core.admin.ban_hang.model.request.AdminHoaDonKhachHangRequest;
import com.shop.server.core.admin.ban_hang.model.request.AdminKhachHangSearchRequest;
import com.shop.server.core.admin.ban_hang.service.AdminPaymentServices;
import com.shop.server.infrastructure.constants.module.MappingConstant;
import com.shop.server.utils.Helper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(MappingConstant.API_ADMIN_PAYMENT)
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

    @GetMapping("/voucher")
    public ResponseEntity<?> getVoucherInKhachHang(@Valid final AdminHoaDonKhachHangRequest request) {
        return Helper.createResponseEntity(adminPaymentServices.getAllVoucherKhachHang(request));
    }

    @GetMapping("/voucher/{id}")
    public ResponseEntity<?> getVoucherInKhachHangById(@PathVariable String id) {
        return Helper.createResponseEntity(null);
    }
    @GetMapping("/payment-method")
    public ResponseEntity<?> getPaymentMethod() {
        return Helper.createResponseEntity(adminPaymentServices.getPhuongThucThanhToan());
    }
}
