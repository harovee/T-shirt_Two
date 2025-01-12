package com.shop.server.core.admin.sale.models.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AdminSaleAndSaleProductDetailRequest {

    private AdminSaleRequest saleRequest;

    private AdminSaleProductRequest saleProductRequest;

}
