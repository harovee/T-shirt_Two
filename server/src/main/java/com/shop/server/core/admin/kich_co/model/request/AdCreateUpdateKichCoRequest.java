package com.shop.server.core.admin.kich_co.model.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AdCreateUpdateKichCoRequest {

    private String ten;

    private Float chieuCaoMin;

    private Float chieuCaoMax;

    private Float canNangMin;

    private Float canNangMax;
}
