package com.shop.server.core.common.base;

import com.shop.server.infrastructure.constants.module.PaginationConstant;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PageableRequest {

    private int page = PaginationConstant.DEFAULT_PAGE;

    private int size = PaginationConstant.DEFAULT_SIZE;

    private String orderBy = PaginationConstant.DEFAULT_ORDER_BY;

    private String sortBy = PaginationConstant.DEFAULT_SORT_BY;

    private String q = PaginationConstant.DEFAULT_Q;

}
