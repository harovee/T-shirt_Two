package com.shop.server.core.admin.point_of_sale.model.request;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AdPOSAddProductsToCartRequest {

    private List<String> idSanPhamChiTiets;

    private String idHoaDonCho;

    private String userEmail;

    private Long soLuong;

}
