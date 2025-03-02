package com.shop.server.core.admin.statistics.model.response;

public interface NumberOrderByStatusResponse {

    Long getNumberSuccessOrder();

    Long getNumberPaymentOrder();

    Long getNumberDeliveryOrder();

    Long getNumberShippingOrder();

    Long getNumberDeliveryWaitingOrder();

    Long getNumberCancelOrder();

    Long getNumberConfirmWaitingOrder();

    Long getNumberWaitingOrder();


}
