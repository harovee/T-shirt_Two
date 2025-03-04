package com.shop.server.core.client.product.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientProductDetailRequest extends ClientProductRequest{

    private String idKichCo;

    private String idMauSac;
}
