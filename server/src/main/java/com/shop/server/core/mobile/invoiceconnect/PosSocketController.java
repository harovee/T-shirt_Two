package com.shop.server.core.mobile.invoiceconnect;

import com.shop.server.core.mobile.invoiceconnect.businessmodel.InvoiceData;
import com.shop.server.core.mobile.invoiceconnect.socketmodel.WebSocketMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
public class PosSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    public PosSocketController(
                               SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    // Xử lý kết nối từ client
    @MessageMapping("/connect")
    public void handleConnection() {
        // Gửi thông báo kết nối thành công
        WebSocketMessage message = new WebSocketMessage();
        message.setType("connection_status");
        message.setData(Map.of("status", "connected"));

        messagingTemplate.convertAndSend("/topic/pos", message);
    }

    // Gửi thông tin giỏ hàng
    @MessageMapping("/cart-info")
    public void handleSendCartInfo(InvoiceData invoiceData) {
        System.out.println("Cart Info: " + invoiceData.toString());

        WebSocketMessage message = new WebSocketMessage();
        message.setType("invoice_update");
        message.setData(invoiceData);

        messagingTemplate.convertAndSend("/topic/pos", message);
    }

    // Xử lý thanh toán
//    @MessageMapping("/payment")
//    public void handlePayment(@Payload PaymentRequest paymentRequest) {
//        // Xử lý yêu cầu thanh toán
//        boolean success = paymentService.processPayment(
//                paymentRequest.getInvoiceId(),
//                paymentRequest.getStatus()
//        );
//
//        // Gửi kết quả về client
//        WebSocketMessage response = new WebSocketMessage();
//        if (success) {
//            response.setType("invoice_clear");
//            response.setData(Map.of("success", true));
//        } else {
//            response.setType("payment_error");
//            response.setData(Map.of("success", false, "message", "Lỗi xử lý thanh toán"));
//        }
//
//        messagingTemplate.convertAndSend("/topic/pos", response);
//    }
//
//    // Xử lý cập nhật địa chỉ
//    @MessageMapping("/update-address")
//    public void handleAddressUpdate(@Payload AddressUpdateRequest request) {
//        // Cập nhật địa chỉ trong hệ thống
//        boolean success = invoiceService.updateAddress(
//                request.getInvoiceId(),
//                request.getAddress()
//        );
//
//        // Không cần gửi phản hồi vì client đã cập nhật UI
//    }
//
//    // Xử lý áp dụng voucher
//    @MessageMapping("/apply-voucher")
//    public void handleVoucherApplication(@Payload VoucherRequest request) {
//        // Áp dụng voucher
//        Invoice updatedInvoice = invoiceService.applyVoucher(
//                request.getInvoiceId(),
//                request.getVoucherCode()
//        );
//
//        // Gửi hóa đơn đã cập nhật về client
//        WebSocketMessage response = new InvoiceUpdateMessage(updatedInvoice);
//        messagingTemplate.convertAndSend("/topic/pos", response);
//    }
}