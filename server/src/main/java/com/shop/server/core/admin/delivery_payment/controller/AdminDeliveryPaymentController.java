package com.shop.server.core.admin.delivery_payment.controller;

import com.shop.server.core.admin.delivery_payment.model.request.AdminCreateDeliveryPaymentRequest;
import com.shop.server.core.admin.delivery_payment.service.AdminDeliveryPaymentService;
import com.shop.server.infrastructure.constants.module.MappingConstant;
import com.shop.server.utils.Helper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(MappingConstant.API_ADMIN_DELIVERY_PAYMENT)
public class AdminDeliveryPaymentController {
    private final AdminDeliveryPaymentService adminDeliveryPaymentService;

    public AdminDeliveryPaymentController(AdminDeliveryPaymentService adminDeliveryPaymentService) {
        this.adminDeliveryPaymentService = adminDeliveryPaymentService;
    }

    @PostMapping()
    ResponseEntity<?> createDeliveryPayment(@RequestBody final AdminCreateDeliveryPaymentRequest request) {
        return Helper.createResponseEntity(adminDeliveryPaymentService.createDeliveryPayment(request));
    }
}
