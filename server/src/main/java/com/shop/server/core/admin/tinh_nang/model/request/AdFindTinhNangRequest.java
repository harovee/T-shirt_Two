package com.shop.server.core.admin.tinh_nang.model.request;

import com.shop.server.core.common.base.PageableRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdFindTinhNangRequest extends PageableRequest {

    private String keyword;
}
