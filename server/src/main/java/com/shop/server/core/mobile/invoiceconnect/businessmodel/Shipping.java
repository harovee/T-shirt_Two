package com.shop.server.core.mobile.invoiceconnect.businessmodel;

import lombok.Data;

@Data
public class Shipping {
    private String method;
    private double cost;
    private String estimatedDelivery;
}