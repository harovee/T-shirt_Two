package com.shop.server.core.admin.hoa_tiet.model.request;

import com.shop.server.core.common.base.PageableRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdFindHoaTietRequest extends PageableRequest {

    private String keyword;
}
