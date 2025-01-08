package com.shop.server.core.admin.bill.controller;

import com.shop.server.core.admin.bill.model.request.AdminFindBillRequest;
import com.shop.server.core.admin.bill.service.AdminBillService;
import com.shop.server.core.admin.staff.models.requests.AdminFindStaffRequest;
import com.shop.server.infrastructure.constants.module.MappingConstant;
import com.shop.server.utils.Helper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(MappingConstant.API_ADMIN_BILL)
@RestController
public class AdminBillController {
    private final AdminBillService adminBillService;

    public AdminBillController(AdminBillService adminBillService) {
        this.adminBillService = adminBillService;
    }

    @GetMapping()
    public ResponseEntity<?> getBills(@Valid final AdminFindBillRequest request) {
        return Helper.createResponseEntity(adminBillService.getBills(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBill(@PathVariable String id) {
        return Helper.createResponseEntity(adminBillService.getDetailBillById(id));
    }
}
