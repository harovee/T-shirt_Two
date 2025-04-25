package com.shop.server.core.mobile.invoiceconnect.businessmodel;
import java.math.BigDecimal;

public class MbInvoice {

    private String idInvoice;

    private String invoiceCode;

//    private List<InvoiceItem> items;

//    private Customer customer;

    private BigDecimal subtotal;
    private BigDecimal tax;
    private BigDecimal total;

    private String paymentMethod;

//    private List<Voucher> vouchers;

//    private ShippingInfo shipping;

    private String status;

}
