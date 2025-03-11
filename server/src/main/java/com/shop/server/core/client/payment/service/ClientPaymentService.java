package com.shop.server.core.client.payment.service;

import com.shop.server.core.client.payment.model.request.ClientPaymentRequest;
import com.shop.server.core.common.base.ResponseObject;
import jakarta.servlet.http.HttpServletRequest;

public interface ClientPaymentService {

    ResponseObject<?> createInvoice (ClientPaymentRequest request);

    ResponseObject<?> createInvoiceWithVnPay (ClientPaymentRequest request, HttpServletRequest httpRequest);
}
