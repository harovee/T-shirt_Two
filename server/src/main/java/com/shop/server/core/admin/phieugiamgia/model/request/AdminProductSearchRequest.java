package com.shop.server.core.admin.phieugiamgia.model.request;

import com.shop.server.core.common.base.PageableRequest;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;

@Getter
@Setter
public class AdminProductSearchRequest extends PageableRequest {
    String search;

}
