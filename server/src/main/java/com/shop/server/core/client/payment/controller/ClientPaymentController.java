package com.shop.server.core.client.payment.controller;

import com.shop.server.core.client.payment.model.request.ClientPaymentRequest;
import com.shop.server.core.client.payment.service.ClientPaymentService;
import com.shop.server.infrastructure.constants.module.MappingConstant;
import com.shop.server.utils.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(MappingConstant.API_CLIENT_PAYMENT)
@CrossOrigin("*")
@RequiredArgsConstructor
public class ClientPaymentController {

    private final ClientPaymentService clientPaymentService;

    @PostMapping()
    public ResponseEntity<?> createInvoice(@RequestBody ClientPaymentRequest request) {
        return Helper.createResponseEntity(clientPaymentService.createInvoice(request));
    }
}
