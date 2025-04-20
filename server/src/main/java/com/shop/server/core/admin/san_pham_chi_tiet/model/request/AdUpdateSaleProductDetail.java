package com.shop.server.core.admin.san_pham_chi_tiet.model.request;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class AdUpdateSaleProductDetail {
    private String id;

    private BigDecimal price;
}
