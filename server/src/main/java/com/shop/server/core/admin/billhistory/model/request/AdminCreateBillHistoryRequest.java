package com.shop.server.core.admin.billhistory.model.request;

import com.shop.server.core.common.base.PageableRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdminCreateBillHistoryRequest extends PageableRequest {
    private String idHoaDon;

    private String hanhDong;

    private String moTa;

    private String trangThai;
}
