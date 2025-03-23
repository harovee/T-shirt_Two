package com.shop.server.core.admin.bill.model.request;

import com.shop.server.core.admin.point_of_sale.model.response.AdOrderDetailResponse;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter

public class AdminSendEmailRequest {
    String email;

    String maHoaDon;

    String trangThai;

    String emailNhanVien;

    String url;

    String ghiChu;
}
