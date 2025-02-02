package com.shop.server.core.admin.billhistory.controller;

import com.shop.server.core.admin.billhistory.model.request.AdminCreateHistoryRequest;
import com.shop.server.core.admin.billhistory.model.request.AdminFindBillHistoryRequest;
import com.shop.server.core.admin.billhistory.service.AdminBillHistoryService;
import com.shop.server.infrastructure.constants.module.MappingConstant;
import com.shop.server.utils.Helper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(MappingConstant.API_ADMIN_BILL_HISTORY)
public class AdminBillHistoryController {
    public final AdminBillHistoryService adminBillHistoryService;

    public AdminBillHistoryController(AdminBillHistoryService adminBillHistoryService) {
        this.adminBillHistoryService = adminBillHistoryService;
    }

    @GetMapping()
    ResponseEntity<?> getAdminBillHistory(@Valid final AdminFindBillHistoryRequest request) {
        return Helper.createResponseEntity(adminBillHistoryService.getAdminBillHistory(request));
    }


    @PostMapping()
    ResponseEntity<?> createAdminBillHistory(@Valid @RequestBody AdminCreateHistoryRequest request) {
        return Helper.createResponseEntity(adminBillHistoryService.createAdminBillHistory(request));
    }
}
