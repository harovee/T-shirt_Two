package com.shop.server.core.admin.sale.models.requests;

import com.shop.server.core.common.base.PageableRequest;
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
public class AdminFindProductSaleModuleRequest extends PageableRequest {

    private String key; // tên

    private String idDanhMuc;

}
