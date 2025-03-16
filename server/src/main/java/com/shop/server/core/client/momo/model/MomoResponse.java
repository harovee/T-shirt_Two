package com.shop.server.core.client.momo.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MomoResponse {
    private String partnerCode;

    private String orderId;

    private String requestId;

    private long amount;

    private long responseTime;

    private String message;

    private int resultCode;

    private String payUrl;

    private String deepLink;

    private String prCodeUrl;
}
