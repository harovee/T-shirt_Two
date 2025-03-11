package com.shop.server.core.admin.billdetail.controller;

import com.shop.server.core.admin.billdetail.model.request.AdminCreateBillDetailRequest;
import com.shop.server.core.admin.billdetail.model.request.AdminFindBillDetailRequest;
import com.shop.server.core.admin.billdetail.model.request.AdminUpdateBillDetailRequest;
import com.shop.server.core.admin.billdetail.service.AdminBillDetailService;
import com.shop.server.infrastructure.constants.module.MappingConstant;
import com.shop.server.utils.Helper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(MappingConstant.API_ADMIN_BILL_DETAIL)
public class AdminBillDetailController {
    private final AdminBillDetailService adminBillDetailService;

    public AdminBillDetailController(AdminBillDetailService adminBillDetailService) {
        this.adminBillDetailService = adminBillDetailService;
    }

    @GetMapping()
    ResponseEntity<?> getAdminBillDetailsByRequest(@Valid final AdminFindBillDetailRequest request) {
        return Helper.createResponseEntity(adminBillDetailService.getBillDetailsByRequest(request));
    }

    @GetMapping("/{id}")
    ResponseEntity<?> getAdminBillDetailById(@PathVariable String id) {
        return Helper.createResponseEntity(adminBillDetailService.getBillDetailById(id));
    }

    @GetMapping("/refund/{maHoaDon}")
    ResponseEntity<?> getAdminBillDetailRefundByMaHD(@PathVariable String maHoaDon) {
        return Helper.createResponseEntity(adminBillDetailService.getBillDetailRefundByMaHD(maHoaDon));
    }

    @PostMapping()
    ResponseEntity<?> createBillDetail(@Valid @RequestBody final AdminCreateBillDetailRequest request) {
        return Helper.createResponseEntity(adminBillDetailService.createBillDetail(request));
    }

    @PutMapping("/{id}")
    ResponseEntity<?> updateBillDetail(@PathVariable String id,@RequestBody AdminUpdateBillDetailRequest request) {
        return Helper.createResponseEntity(adminBillDetailService.updateBillDetail(id, request));
    }
}
