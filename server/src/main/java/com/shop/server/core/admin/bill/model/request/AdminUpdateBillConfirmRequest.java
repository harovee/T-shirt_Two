package com.shop.server.core.admin.bill.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdminUpdateBillConfirmRequest {

    private BigDecimal tienShip;

    private BigDecimal tienGiam;

    private BigDecimal tongTien;
}
