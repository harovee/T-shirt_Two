package com.shop.server.core.admin.mau_sac.model.request;

import com.shop.server.core.common.base.PageableRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdFindMauSacRequest extends PageableRequest {

    private String keyword;
}
