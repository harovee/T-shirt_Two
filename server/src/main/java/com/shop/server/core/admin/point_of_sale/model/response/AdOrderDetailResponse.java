package com.shop.server.core.admin.point_of_sale.model.response;

import com.shop.server.core.common.base.BaseResponse;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AdOrderDetailResponse {
    String catalog;

    String tenMauSac;

    String kichCo;

    String tenSanPham;

    Double giaHienTai;

    String SoLuong;
}
