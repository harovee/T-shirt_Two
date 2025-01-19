package com.shop.server.core.admin.sale.models.requests;

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
public class AdminSaleProductRequest {

    private List<String> idSanPhamChiTiets;

    private String idDotGiamGia;

    private String loaiGiamGia;

    private Double giaTriGiamGia;

    private Double giaTriGiamToiDa;

    private String nhanVien;


}
