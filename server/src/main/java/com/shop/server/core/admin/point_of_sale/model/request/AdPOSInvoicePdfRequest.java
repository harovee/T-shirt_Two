package com.shop.server.core.admin.point_of_sale.model.request;

import com.shop.server.core.admin.point_of_sale.model.response.AdOrderDetailResponse;
import com.shop.server.core.admin.point_of_sale.model.response.AdPOSOrderDetailResponse;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AdPOSInvoicePdfRequest {
    String idKhachHang;

    String idNhanVien;

    String idHoaDon;

    List<AdOrderDetailResponse> products;

    BigDecimal tongTien;

    BigDecimal phiVanChuyen;

    BigDecimal giamGia;
}
