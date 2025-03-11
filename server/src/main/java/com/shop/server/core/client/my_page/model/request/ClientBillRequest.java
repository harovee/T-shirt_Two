package com.shop.server.core.client.my_page.model.request;

import com.shop.server.core.common.base.PageableRequest;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ClientBillRequest extends PageableRequest {

    private String keyword;

    private String trangThai;

    private String idKhachHang;
}
