package com.shop.server.core.admin.product.models.requests;

import com.shop.server.core.common.base.PageableRequest;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AdGetSanPhamChiTietRequest extends PageableRequest {
    private String id;
}
