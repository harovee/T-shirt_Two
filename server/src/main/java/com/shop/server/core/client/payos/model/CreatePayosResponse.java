package com.shop.server.core.client.payos.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreatePayosResponse {

    private String checkoutUrl;

    private Long orderCode;

    private String qrCode;

    private Integer amount;

}
