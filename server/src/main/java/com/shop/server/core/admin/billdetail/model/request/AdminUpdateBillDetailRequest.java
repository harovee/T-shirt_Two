package com.shop.server.core.admin.billdetail.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdminUpdateBillDetailRequest {
    private Short soLuong;

    private String idHoaDonChiTiet;
}
