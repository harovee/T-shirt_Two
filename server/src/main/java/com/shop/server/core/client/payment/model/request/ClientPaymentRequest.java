package com.shop.server.core.client.payment.model.request;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
public class ClientPaymentRequest {
    String diaChiNguoiNhan;

    String ghiChu;

    String soDienThoai;

    String tenNguoiNhan;

    BigDecimal tienGiam;

    BigDecimal tienShip;

    BigDecimal tongTien;

    String idKhachHang;

    String idNhanVien;

    String idPhieuGiamGia;

    String paymentMethod;

    String tinh;

    String huyen;

    String xa;

    List<ClientInvoiceDetailRequest> listSanPhamChiTiets;

}
