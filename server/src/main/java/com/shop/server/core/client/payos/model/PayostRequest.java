package com.shop.server.core.client.payos.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PayostRequest {
    private int amount;
    private String description;
    private String buyerName;
    private String buyerEmail;
    private String buyerPhone;
}
