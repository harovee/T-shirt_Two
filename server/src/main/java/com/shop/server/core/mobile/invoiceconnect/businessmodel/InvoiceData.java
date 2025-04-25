package com.shop.server.core.mobile.invoiceconnect.businessmodel;

import lombok.Data;

import java.util.List;

@Data
public class InvoiceData {
    private String id;
    private List<Item> items;
    private double subtotal;
    private double tax;
    private double total;
    private CustomerInfo customerInfo;  
    private String paymentMethod;
    private Shipping shipping;
    private List<Voucher> vouchers;
    private double guestMoney;
    private double transferMoney;
}
