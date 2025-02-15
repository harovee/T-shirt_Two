package com.shop.server.core.admin.payment_method.controller;

import com.shop.server.core.admin.payment_method.service.AdminPaymentMethodService;
import com.shop.server.infrastructure.constants.module.MappingConstant;
import com.shop.server.utils.Helper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(MappingConstant.API_ADMIN_PAYMENT_METHOD)
public class AdminPaymentMethodController {
    public final AdminPaymentMethodService adminPaymentMethodService;

    public AdminPaymentMethodController(AdminPaymentMethodService adminPaymentMethodService) {
        this.adminPaymentMethodService = adminPaymentMethodService;
    }

    @GetMapping()
    ResponseEntity<?> getAdminPaymentMethods() {
        return Helper.createResponseEntity(adminPaymentMethodService.getAdminPaymentMethod());
    }
}
