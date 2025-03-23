package com.shop.server.core.client.momo.controller;

import com.shop.server.core.client.momo.model.request.ClientMomoRequest;
import com.shop.server.core.client.momo.services.IMomoService;
import com.shop.server.core.client.payment.service.impl.ClientPaymentServiceImpl;
import com.shop.server.core.common.base.ResponseObject;
import com.shop.server.infrastructure.constants.module.MappingConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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

    @GetMapping("momo-callback")
    public ResponseEntity<?> momoCallback(@RequestParam Map<String, String>  request) {
        Integer resultCode = Integer.valueOf(request.get("resultCode"));
        String orderId = request.get("orderId");
        if (resultCode == 0) {
            clientPaymentService.handleMomoSuccess(request,orderId);
            return  ResponseEntity.ok(new ResponseObject<>(null, HttpStatus.OK, "Thanh toán qua momo thành công"));
        }
        else {
            clientPaymentService.rollbackInvoiceOnMomoFailure(orderId,request);
            return ResponseEntity.ok(new ResponseObject<>(null, HttpStatus.BAD_REQUEST, "Thanh toán thất bại"));
        }
    }
}
