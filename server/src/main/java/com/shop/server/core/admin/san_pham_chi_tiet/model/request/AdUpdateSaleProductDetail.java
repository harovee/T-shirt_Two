package com.shop.server.core.admin.san_pham_chi_tiet.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AdUpdateSaleProductDetail {
    private String id;

    private BigDecimal price;
}
