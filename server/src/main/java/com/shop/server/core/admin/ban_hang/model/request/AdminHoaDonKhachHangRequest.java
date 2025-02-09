package com.shop.server.core.admin.ban_hang.model.request;

import com.shop.server.core.common.base.PageableRequest;
import com.shop.server.entities.main.HoaDon;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.query.sql.internal.ParameterRecognizerImpl;

import java.math.BigDecimal;

@Getter
@Setter
public class AdminHoaDonKhachHangRequest  extends PageableRequest {

    private String idKhachHang;

    private BigDecimal tongTien;

    private String idHoaDon;

    private String idPhieuGiamGia;
}
