package com.shop.server.core.admin.kich_co.model.request;

import com.shop.server.core.common.base.PageableRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdFindKichCoRequest extends PageableRequest {

    private String keyword;
}
