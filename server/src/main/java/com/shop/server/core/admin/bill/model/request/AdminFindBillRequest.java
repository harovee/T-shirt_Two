package com.shop.server.core.admin.bill.model.request;

import com.shop.server.core.common.base.PageableRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdminFindBillRequest extends PageableRequest {
    private String keyword;
}
