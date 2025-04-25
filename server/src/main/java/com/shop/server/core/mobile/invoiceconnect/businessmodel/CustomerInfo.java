package com.shop.server.core.mobile.invoiceconnect.businessmodel;

import lombok.Data;

@Data
public class CustomerInfo {
    private String name;
    private String phone;
    private Address address;
    private String email;
}
