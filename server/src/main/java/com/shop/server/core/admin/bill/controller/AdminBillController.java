package com.shop.server.core.admin.bill.controller;

import com.shop.server.core.admin.bill.model.request.AdminFindBillRequest;
import com.shop.server.core.admin.bill.model.request.AdminSaveBillRequest;
import com.shop.server.core.admin.bill.model.request.AdminUpdateBillRequest;
import com.shop.server.core.admin.bill.service.AdminBillService;
import com.shop.server.infrastructure.constants.module.MappingConstant;
import com.shop.server.utils.Helper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @PostMapping()
    public ResponseEntity<?> createBill(@Valid @RequestBody final AdminSaveBillRequest request, BindingResult result) {
        return Helper.createResponseEntity(adminBillService.createBill(request, result));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateBillStatus(@PathVariable String id,
                                              @Valid @RequestBody final AdminUpdateBillRequest request,
                                              BindingResult result) {
        return Helper.createResponseEntity(adminBillService.updateBill(id, request, result));
    }

    @PutMapping("/status-bill/{id}")
    public ResponseEntity<?> changeBillStatus(@PathVariable String id,
                                              @Valid @RequestBody final AdminUpdateBillRequest request) {
        return Helper.createResponseEntity(adminBillService.changeStatusBill(id, request));
    }
}
