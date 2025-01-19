package com.shop.server.core.admin.tay_ao.model.request;

import com.shop.server.core.common.base.PageableRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdFindTayAoRequest extends PageableRequest {

    private String keyword;
}
