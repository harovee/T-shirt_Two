package com.shop.server.core.admin.billdetail.controller;

import com.shop.server.core.admin.billdetail.model.request.AdminFindBillDetailRequest;
import com.shop.server.core.admin.billdetail.service.AdminBillDetailService;
import com.shop.server.infrastructure.constants.module.MappingConstant;
import com.shop.server.utils.Helper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
}
