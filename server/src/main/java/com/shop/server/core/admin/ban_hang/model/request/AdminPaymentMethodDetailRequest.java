package com.shop.server.core.admin.ban_hang.model.request;

import com.shop.server.entities.main.HoaDon;
import com.shop.server.entities.main.PhuongThucThanhToan;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class AdminPaymentMethodDetailRequest {
    private String idHoaDon;

    private String idPhuongThucThanhToan;

    private String idNhanVien;

    private BigDecimal tienKhachDua;

    private BigDecimal soTienDu;

    private BigDecimal tienChuyenKhoan;

    private String maGiaoDich;

    private String moTa;
}
