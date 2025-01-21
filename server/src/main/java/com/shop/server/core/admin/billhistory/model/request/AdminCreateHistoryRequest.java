package com.shop.server.core.admin.billhistory.model.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AdminCreateHistoryRequest {
    private String idHoaDon;

    private String hanhDong;

    private String moTa;

    private String trangThai;
}
