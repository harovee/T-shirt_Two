package com.shop.server.core.client.payos.controller;

import com.shop.server.core.client.payos.model.PayostRequest;
import com.shop.server.core.client.payos.services.Impl.PayosService;
import com.shop.server.infrastructure.constants.module.MappingConstant;
import com.shop.server.utils.Helper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.payos.type.CheckoutResponseData;

@RestController
@CrossOrigin("*")
//@RequiredArgsConstructor
@RequestMapping(MappingConstant.API_CLIENT_PAYMENT)
public class PayosController {

        private  PayosService paymentService;

        @Autowired
        public PayosController(PayosService paymentService) {
            this.paymentService = paymentService;
        }

        @PostMapping("/payos")
        public ResponseEntity<?> createPaymentLink(@RequestBody PayostRequest request) {

            return Helper.createResponseEntity(paymentService.createPaymentLink(request));
        }

//        @GetMapping("/verify/{orderCode}")
//        public ResponseEntity<PaymentVerificationResponse> verifyPayment(@PathVariable int orderCode) {
//            PaymentVerificationResponse verificationResponse = paymentService.verifyPayment(orderCode);
//            return ResponseEntity.ok(verificationResponse);
//        }
}
