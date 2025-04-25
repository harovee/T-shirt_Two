package com.shop.server.core.mobile.invoiceconnect;

import com.shop.server.core.admin.point_of_sale.service.PointOfSaleServiceIml;
import com.shop.server.core.mobile.invoiceconnect.businessmodel.InvoiceInfo;
import lombok.*;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

@Controller
@CrossOrigin("*")
public class InvoiceSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final PointOfSaleServiceIml pointOfSaleService;

    public InvoiceSocketController(SimpMessagingTemplate messagingTemplate,
                                   PointOfSaleServiceIml pointOfSaleService) {
        this.messagingTemplate = messagingTemplate;
        this.pointOfSaleService = pointOfSaleService;
    }

    // Kết nối hóa đơn (MB gửi invoiceId để bắt đầu kết nối)
    @MessageMapping("/connect-invoice") // ok
    public void startConnectInvoice(InvoiceInfo invoice) {
        messagingTemplate.convertAndSend("/topic/connect-confirm/" + invoice.getInvoiceId(), invoice);
    }

    // FE phản hồi sự xác nhận
    @MessageMapping("/connect-confirm") // ok
    public void connectInvoice(InvoiceInfo invoice) {
        messagingTemplate.convertAndSend("/topic/invoice/" + invoice.getInvoiceId(), invoice);
    }

    // Ngắt kết nối từ quầy
    @MessageMapping("/disconnect-mobile") // ok
    public void disConnectMobile(String invoiceId) {
        messagingTemplate.convertAndSend("/topic/disconnect-mobile/" + invoiceId, invoiceId);
    }

    // Ngắt kết nối từ MB, gửi thông báo cho quầy
    @MessageMapping("/disconnect-client") // ok
    public void disConnectClient(String invoiceId) {
        messagingTemplate.convertAndSend("/topic/disconnect-client/" + invoiceId, invoiceId);
    }

    // Cập nhật giỏ hàng cho MB
    @MessageMapping("/update-cart-mb") //ok
    public void updateCartMb(String invoiceId) {
        messagingTemplate.convertAndSend("/topic/update-cart-mb/" + invoiceId, invoiceId);
    }

    // Cập nhật giỏ hàng - số lượng sp từ MB
    @MessageMapping("/update-cart")
    public void updateProductQuantityInCart(UpdateQuantityInput input) {
        messagingTemplate.convertAndSend("/topic/update-cart/" + input.invoiceId, input);
    }

    // Cập nhật giỏ hàng - xóa sp từ MB
    @MessageMapping("/delete-pd-cart")
    public void updateDeleteProductInCart(String input) {
        String[] inputs = input.split("\\s*,\\s*");
        messagingTemplate.convertAndSend("/topic/delete-pd-cart/" + inputs[0], inputs[1]);
    }

    // Yều cầu xác nhận thanh toán từ quầy
    @MessageMapping("/confirm-payment") // ok
    public void confirmPayment(String invoiceId) {
        messagingTemplate.convertAndSend("/topic/payment/" + invoiceId, invoiceId);
    }

    // Phản hồi xác nhận thanh toán từ MB
    @MessageMapping("/confirm-payment-mb") // ok
    public void confirmPaymentFromMobile(TransactionInfo transactionInfo) {
        messagingTemplate.convertAndSend("/topic/confirm-payment/" + transactionInfo.getInvoiceId(),
                transactionInfo.getTransactionStatus());
    }

    // Gửi thông tin giao dịch từ quầy
    @MessageMapping("/transaction-info")
    public void sendTransactionInfo(TransactionInfo transactionInfo) {
        messagingTemplate.convertAndSend("/topic/transaction-info/" + transactionInfo.getInvoiceId(),
                transactionInfo.getTransactionStatus());
    }


    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    public static class UpdateQuantityInput {
        String invoiceId;
        String productId;
        Integer quantity;
        String orderDetailId;
    }
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    public static class TransactionInfo {
        String invoiceId;
        Boolean transactionStatus;
    }


}
