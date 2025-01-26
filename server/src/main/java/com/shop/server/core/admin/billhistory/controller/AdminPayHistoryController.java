package com.shop.server.core.admin.billhistory.controller;

import com.shop.server.core.admin.billhistory.model.request.AdminFindPayHistoryRequest;
import com.shop.server.core.admin.billhistory.service.AdminPayHistoryService;
import com.shop.server.infrastructure.constants.module.MappingConstant;
import com.shop.server.utils.Helper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(MappingConstant.API_ADMIN_PAY_HISTORY)
public class AdminPayHistoryController {
    public final AdminPayHistoryService adminPayHistoryService;

    public AdminPayHistoryController(AdminPayHistoryService adminPayHistoryService) {
        this.adminPayHistoryService = adminPayHistoryService;
    }

    @GetMapping
    ResponseEntity<?> getAdminPayHistory(@Valid final AdminFindPayHistoryRequest request) {
        return Helper.createResponseEntity(adminPayHistoryService.getPayHistory(request));
    }

}
