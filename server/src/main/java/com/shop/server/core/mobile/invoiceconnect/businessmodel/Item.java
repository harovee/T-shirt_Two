package com.shop.server.core.mobile.invoiceconnect.businessmodel;

import lombok.Data;

@Data
public class Item {
    private String id;
    private String name;
    private int quantity;
    private double price;
    private double total;
    private String image;
    private String size;
    private String color;
    private String sku;
}

