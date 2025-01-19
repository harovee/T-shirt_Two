package com.shop.server.core.admin.sale.models.requests;

import com.shop.server.core.common.base.PageableRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdminFindSaleProductDetailRequest extends PageableRequest {

    private String keyword; // mã, tên

    private String idDotGiamGia;

}
