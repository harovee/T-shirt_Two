package com.shop.server.core.client.payment.model.request;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class ClientInvoiceDetailRequest {
    String idSanPhamChiTiet;

    Long soLuong;

    BigDecimal gia;
}
