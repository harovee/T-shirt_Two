package com.shop.server.core.admin.client.models.requests;

import com.shop.server.core.common.base.PageableRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdminFindClientRequest extends PageableRequest {

    private String keyword;

    private Integer status;

}
