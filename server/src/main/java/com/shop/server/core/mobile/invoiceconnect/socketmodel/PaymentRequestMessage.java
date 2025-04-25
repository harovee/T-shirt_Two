package com.shop.server.core.mobile.invoiceconnect.socketmodel;

public class PaymentRequestMessage extends WebSocketMessage {

    public PaymentRequestMessage(Object request) {
        setType("payment_request");
        setData(request);
    }

}
