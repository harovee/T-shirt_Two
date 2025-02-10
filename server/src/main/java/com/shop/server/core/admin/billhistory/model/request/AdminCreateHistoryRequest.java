package com.shop.server.core.admin.billhistory.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AdminCreateHistoryRequest {

    private String idHoaDon;

    private String hanhDong;

    private String moTa;

    private String trangThai;

    private String nguoiTao;

    private Long ngayTao;
}
