package com.shop.server.core.mobile.invoiceconnect.businessmodel;

import lombok.Data;

@Data
public class Voucher {
    private String code;
    private double discount;
    private String type;
    private double maxDiscount;
}