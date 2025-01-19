package com.shop.server.core.admin.thuong_hieu.model.request;

import com.shop.server.core.common.base.PageableRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdFindThuongHieuRequest extends PageableRequest {

    private String keyword;
}
