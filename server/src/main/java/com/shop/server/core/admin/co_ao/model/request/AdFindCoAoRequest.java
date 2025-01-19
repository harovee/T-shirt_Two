package com.shop.server.core.admin.co_ao.model.request;

import com.shop.server.core.common.base.PageableRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdFindCoAoRequest extends PageableRequest {

    private String keyword;
}
