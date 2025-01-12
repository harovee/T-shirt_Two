package com.shop.server.core.admin.product.models.requests;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AdminProductRequest {
    private String ten;

    private String moTa;

    private Integer trangThai;

    private String idDanhMuc;
}
