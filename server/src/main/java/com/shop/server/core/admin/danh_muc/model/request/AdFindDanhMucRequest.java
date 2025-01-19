package com.shop.server.core.admin.danh_muc.model.request;

import com.shop.server.core.common.base.PageableRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdFindDanhMucRequest extends PageableRequest {

    private String keyword;
}
