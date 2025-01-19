package com.shop.server.core.admin.kieu_dang.model.request;

import com.shop.server.core.common.base.PageableRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdFindKieuDangRequest extends PageableRequest {

    private String keyword;
}
