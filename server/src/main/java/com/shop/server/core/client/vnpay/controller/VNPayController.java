package com.shop.server.core.client.vnpay.controller;

import com.shop.server.core.client.payment.model.request.ClientPaymentRequest;
import com.shop.server.core.client.payment.service.impl.ClientPaymentServiceImpl;
import com.shop.server.core.client.vnpay.model.VNPayRequest;
import com.shop.server.core.client.vnpay.model.VNPayResponse;
import com.shop.server.core.client.vnpay.service.VNPayService;
import com.shop.server.core.common.base.ResponseObject;
import com.shop.server.infrastructure.constants.module.MappingConstant;
import com.shop.server.utils.Helper;
import com.shop.server.utils.VNPayUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin("*")
@RequiredArgsConstructor
@RequestMapping(MappingConstant.API_CLIENT_PAYMENT)
public class VNPayController {

    private final VNPayService paymentService;

    private final ClientPaymentServiceImpl clientPaymentService;

    @GetMapping("/vn-pay/{idHoaDon}")
    public ResponseObject<VNPayResponse> pay(@RequestBody VNPayRequest vnPayRequest, HttpServletRequest request, @PathVariable("idHoaDon") String idHoaDon) {
        String ipAddress = VNPayUtil.getIpAddress(request);
        return new ResponseObject<>(paymentService.createVnPayPayment(vnPayRequest, ipAddress, idHoaDon), HttpStatus.OK, "Success");
    }

    @GetMapping("/vn-pay")
    public ResponseObject<VNPayResponse> payVNPay(@Valid final VNPayRequest vnPayRequest, HttpServletRequest request) {
        String ipAddress = VNPayUtil.getIpAddress(request);
        return new ResponseObject<>(paymentService.createPaymentWithVNPay(vnPayRequest, ipAddress), HttpStatus.OK, "Success");
    }

    @PostMapping("/vn-pay-callback")
    public ResponseEntity<?> vnpayCallback(@RequestBody ClientPaymentRequest request) {
         //clientPaymentService.handlePayMentVnPaySuccess( request);
         return Helper.createResponseEntity(clientPaymentService.handlePayMentVnPaySuccess( request));
    }
}

