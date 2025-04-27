package com.shop.server.core.mobile.invoiceconnect.businessmodel;

import lombok.Data;

@Data
public class Address {
    private String province;
    private String district;
    private String ward;
    private String detail;
}