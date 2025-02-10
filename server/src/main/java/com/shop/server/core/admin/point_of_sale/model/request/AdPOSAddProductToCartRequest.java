package com.shop.server.core.admin.point_of_sale.model.request;

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
public class AdPOSAddProductToCartRequest {

    private String idSanPhamChiTiet;

    private String idHoaDonCho;

    private String userEmail;

    private Long soLuong = 1L;

}