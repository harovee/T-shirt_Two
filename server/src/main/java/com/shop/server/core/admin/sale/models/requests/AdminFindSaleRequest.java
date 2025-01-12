package com.shop.server.core.admin.sale.models.requests;

import com.shop.server.core.common.base.PageableRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdminFindSaleRequest extends PageableRequest {

    private String keyword; // mã, tên

    private String loai;

    private Long ngayBatDau;

    private Long ngayKetThuc;

    private String trangThai;

}
