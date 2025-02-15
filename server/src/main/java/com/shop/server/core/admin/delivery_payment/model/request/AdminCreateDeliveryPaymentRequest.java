package com.shop.server.core.admin.delivery_payment.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdminCreateDeliveryPaymentRequest {

    private String idHoaDon;

    private String idPhuongThucThanhToan;

    private String maGiaoDich;

    private BigDecimal tienKhachDua;

    private BigDecimal soTienDu;

    private String ghiChu;

    //tao ls hoa don

    private String hanhDong;

    private String moTa;

    private String nguoiTao;

    private Long ngayTao;
}
