package com.shop.server.core.client.payos.services.Impl;

import com.shop.server.core.client.payos.model.CreatePayosResponse;
import com.shop.server.core.client.payos.model.PayostRequest;
import com.shop.server.core.common.base.ResponseObject;
import com.shop.server.infrastructure.config.database.PayosConfig;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import vn.payos.PayOS;
import vn.payos.type.CheckoutResponseData;
import vn.payos.type.PaymentData;

import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Slf4j
public class PayosService {

    private final PayOS  payOS;

    @Value("${vietqr.return_url_success}")
    private String returnUrlSuccess;

    @Value("${vietqr.return_url_cancel}")
    private String returnUrlCancel;

    public PayosService(PayOS payOS) {
        super();
        this.payOS = payOS;
    }


    public ResponseObject<CreatePayosResponse> createPaymentLink(PayostRequest request) {
        try {
            // Cấu hình thông tin thanh toán
            String currentTimeString = String.valueOf(new Date().getTime());
            long orderCode = Long.parseLong(currentTimeString.substring(currentTimeString.length() - 6));
            PaymentData paymentData = PaymentData.builder()
                    .orderCode(orderCode)
                    .amount(request.getAmount())
                    .description(request.getDescription())
                    .cancelUrl(returnUrlSuccess)
                    .returnUrl(returnUrlCancel)
                    .buyerName(request.getBuyerName())
                    .buyerEmail(request.getBuyerEmail())
                    .buyerPhone(request.getBuyerPhone())
                    .build();

            // Tạo link thanh toán
            CheckoutResponseData checkoutResponseData  = payOS.createPaymentLink(paymentData);
            CreatePayosResponse createPayosResponse = new CreatePayosResponse();
            createPayosResponse.setCheckoutUrl(checkoutResponseData.getCheckoutUrl());
            createPayosResponse.setQrCode(checkoutResponseData.getQrCode());
            createPayosResponse.setOrderCode(checkoutResponseData.getOrderCode());
            createPayosResponse.setAmount(checkoutResponseData.getAmount());
            return new ResponseObject<>(createPayosResponse, HttpStatus.OK, "Lấy Data thành công");
        } catch (Exception e) {
            log.error("Payment link creation error", e);
            return new ResponseObject<>(null, HttpStatus.BAD_REQUEST, "Lỗi tạo link thanh toán " + e.getMessage());
        }
    }

//    public PaymentVerificationResponse verifyPayment(int orderCode) {
//        try {
//            // Kiểm tra trạng thái thanh toán
//            return payOS.getPaymentStatus(orderCode);
//        } catch (Exception e) {
//            throw new PaymentException("Lỗi xác minh thanh toán", e);
//        }
//    }

//
//        private @NonNull long generateUniqueOrderCode() {
//            // PayOS typically requires order codes to be integers between 1 and 9999999
//            int randomNum = ThreadLocalRandom.current().nextInt(1, 10000000);
//            return randomNum;
//        }
}
