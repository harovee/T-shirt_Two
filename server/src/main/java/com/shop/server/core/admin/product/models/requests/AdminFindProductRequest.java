package com.shop.server.core.admin.product.models.requests;

import com.shop.server.core.common.base.PageableRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminFindProductRequest extends PageableRequest {
    private String keyword;

    private Integer status;
}
