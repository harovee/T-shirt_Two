package com.shop.server.core.client.vnpay.service;

import com.shop.server.core.client.vnpay.model.VNPayRequest;
import com.shop.server.core.client.vnpay.model.VNPayResponse;
import com.shop.server.infrastructure.config.database.VNPayConfig;
import com.shop.server.utils.VNPayUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class VNPayService {
    private final VNPayConfig vnPayConfig;

    public VNPayResponse createVnPayPayment(VNPayRequest request, String ipAddress, String idHoaDon) {
//        long amount = Long.valueOf(request.getAmount()) * 100L;
        long amount = (long) (Double.parseDouble(request.getAmount()) * 100);
        String bankCode = request.getBankCode();
        Map<String, String> vnpParamsMap = vnPayConfig.getVNPayConfig(idHoaDon);
        vnpParamsMap.put("vnp_Amount", String.valueOf(amount));
        if (bankCode != null && !bankCode.isEmpty()) {
            vnpParamsMap.put("vnp_BankCode", bankCode);
        }
        vnpParamsMap.put("vnp_IpAddr", ipAddress);
        //build query url
        String queryUrl = VNPayUtil.getPaymentURL(vnpParamsMap, true);
        String hashData = VNPayUtil.getPaymentURL(vnpParamsMap, false);
        String vnpSecureHash = VNPayUtil.hmacSHA512(vnPayConfig.getSecretKey(), hashData);
        queryUrl += "&vnp_SecureHash=" + vnpSecureHash;
        String paymentUrl = vnPayConfig.getVnp_PayUrl() + "?" + queryUrl;
        return new VNPayResponse("OK", "success", paymentUrl);
    }

    public VNPayResponse createPaymentWithVNPay(VNPayRequest request, String ipAddress) {
//        long amount = Long.valueOf(request.getAmount()) * 100L;
        long amount = (long) (Double.parseDouble(request.getAmount()) * 100);
        String bankCode = request.getBankCode();
        String idHoaDon = UUID.randomUUID().toString();
        Map<String, String> vnpParamsMap = vnPayConfig.getVNPayConfig(idHoaDon);
        vnpParamsMap.put("vnp_Amount", String.valueOf(amount));
        if (bankCode != null && !bankCode.isEmpty()) {
            vnpParamsMap.put("vnp_BankCode", bankCode);
        }
        vnpParamsMap.put("vnp_IpAddr", ipAddress);
        //build query url
        String queryUrl = VNPayUtil.getPaymentURL(vnpParamsMap, true);
        String hashData = VNPayUtil.getPaymentURL(vnpParamsMap, false);
        String vnpSecureHash = VNPayUtil.hmacSHA512(vnPayConfig.getSecretKey(), hashData);
        queryUrl += "&vnp_SecureHash=" + vnpSecureHash;
        String paymentUrl = vnPayConfig.getVnp_PayUrl() + "?" + queryUrl;
        return new VNPayResponse("OK", "success", paymentUrl);
    }
}