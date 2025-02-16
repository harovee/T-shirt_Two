package com.shop.server.core.admin.delivery_payment.service;

import com.shop.server.core.admin.delivery_payment.model.request.AdminCreateDeliveryPaymentRequest;
import com.shop.server.core.common.base.ResponseObject;

public interface AdminDeliveryPaymentService {
    ResponseObject<?> createDeliveryPayment(AdminCreateDeliveryPaymentRequest request);
}
