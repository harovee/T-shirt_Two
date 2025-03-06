package com.shop.server.core.client.vnpay.controller;


import com.shop.server.core.client.vnpay.model.VNPayResponse;
import com.shop.server.core.client.vnpay.service.VNPayService;
import com.shop.server.core.common.base.ResponseObject;
import com.shop.server.infrastructure.constants.module.MappingConstant;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin("*")
@RequiredArgsConstructor
@RequestMapping(MappingConstant.API_CLIENT_PAYMENT)
public class VNPayController {

    private final VNPayService paymentService;

    @GetMapping("/vn-pay")
    public ResponseObject<VNPayResponse> pay(HttpServletRequest request) {
        return new ResponseObject<>(paymentService.createVnPayPayment(request),HttpStatus.OK, "Success");
    }

    @GetMapping("/vn-pay-callback")
    public ResponseObject<VNPayResponse> payCallbackHandler(HttpServletRequest request) {
        String status = request.getParameter("vnp_ResponseCode");
        if (status.equals("00")) {
            return new ResponseObject<>(new VNPayResponse("00", "Success", ""),HttpStatus.OK, "Success" );
        } else {
            return new ResponseObject<>(null,HttpStatus.BAD_REQUEST, "Failed");
        }
    }
}
