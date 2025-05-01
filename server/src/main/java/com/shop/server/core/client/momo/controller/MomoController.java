package com.shop.server.core.client.momo.controller;

import com.shop.server.core.client.momo.model.request.ClientMomoRequest;
import com.shop.server.core.client.momo.services.IMomoService;
import com.shop.server.core.client.payment.model.request.ClientPaymentRequest;
import com.shop.server.core.client.payment.service.impl.ClientPaymentServiceImpl;
import com.shop.server.infrastructure.constants.module.MappingConstant;
import com.shop.server.utils.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
@RequestMapping(MappingConstant.API_CLIENT_PAYMENT)
public class MomoController {

    private final IMomoService momoServices;
    private final ClientPaymentServiceImpl  clientPaymentService;

    @PostMapping("momo")
    ResponseEntity<?> createMomo(@RequestBody ClientMomoRequest request) {
        return new ResponseEntity<>(momoServices.createMomo(request), HttpStatus.OK);
    }

    @PostMapping("mo-mo")
    ResponseEntity<?> createUrlMomo(@RequestBody ClientMomoRequest request) {
        return new ResponseEntity<>(momoServices.createUrlMomo(request), HttpStatus.OK);
    }

    @PostMapping("momo-callback")
    public ResponseEntity<?> momoCallback(@RequestBody ClientPaymentRequest request) {
            return Helper.createResponseEntity(clientPaymentService.handlePayMentMomoSuccess(request));
    }
}
