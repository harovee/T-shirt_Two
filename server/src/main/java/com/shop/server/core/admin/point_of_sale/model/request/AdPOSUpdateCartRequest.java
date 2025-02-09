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
public class AdPOSUpdateCartRequest {

    private String idHoaDonChiTiet;

    private Long soLuongBanTruoc;

    private Long soLuongBanSau;


}
