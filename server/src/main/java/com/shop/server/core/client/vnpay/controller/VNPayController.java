package com.shop.server.core.client.vnpay.controller;

import com.shop.server.core.client.payment.service.impl.ClientPaymentServiceImpl;
import com.shop.server.core.client.vnpay.model.VNPayRequest;
import com.shop.server.core.client.vnpay.model.VNPayResponse;
import com.shop.server.core.client.vnpay.service.VNPayService;
import com.shop.server.core.common.base.ResponseObject;
import com.shop.server.infrastructure.constants.module.MappingConstant;
import com.shop.server.utils.VNPayUtil;
import jakarta.servlet.http.HttpServletRequest;
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

    @GetMapping("/vn-pay-callback")
    public ResponseEntity<?> vnpayCallback(@RequestParam Map<String, String> params, HttpServletRequest request) {
        String vnpResponseCode = params.get("vnp_ResponseCode");
        String invoiceId = params.get("vnp_TxnRef");

        try {
            if ("00".equals(vnpResponseCode)) {
                // Thanh toán thành công
                clientPaymentService.handleVnPaySuccess(params, invoiceId);
                return ResponseEntity.ok(new ResponseObject<>(null, HttpStatus.OK, "Thanh toán thành công"));
            } else {
                // Thanh toán thất bại - tiến hành rollback dữ liệu
                clientPaymentService.rollbackInvoiceOnVnPayFailure(invoiceId, params);

                // Trả về thông báo thất bại với mã lỗi cụ thể
                String errorMessage = "Thanh toán thất bại: " + clientPaymentService.getVnPayErrorMessage(vnpResponseCode);
                return ResponseEntity.ok(new ResponseObject<>(null, HttpStatus.OK, errorMessage));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseObject<>(null, HttpStatus.INTERNAL_SERVER_ERROR,
                            "Lỗi xử lý thanh toán: " + e.getMessage()));
        }
    }
}

