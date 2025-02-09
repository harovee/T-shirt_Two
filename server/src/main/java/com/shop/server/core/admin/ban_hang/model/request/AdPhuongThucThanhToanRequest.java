package com.shop.server.core.admin.ban_hang.model.request;

import com.shop.server.entities.main.HoaDon;
import com.shop.server.entities.main.PhuongThucThanhToan;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class AdPhuongThucThanhToanRequest {
    private HoaDon hoaDon;

    private PhuongThucThanhToan phuongThucThanhToan;

    private BigDecimal tienKhachDua;

    private BigDecimal soTienDu;

    private String maGiaoDich;
}
