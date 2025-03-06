package com.shop.server.core.client.vnpay.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VNPayResponse {
    public String code;
    public String message;
    public String paymentUrl;
}