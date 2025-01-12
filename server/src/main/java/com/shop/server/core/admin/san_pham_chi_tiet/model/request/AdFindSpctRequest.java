package com.shop.server.core.admin.san_pham_chi_tiet.model.request;

import com.shop.server.core.common.base.PageableRequest;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
public class AdFindSpctRequest extends PageableRequest {
    private String keyword;

    private String id;

    private Integer trangThai;

    private BigDecimal gia;

    private String idChatLieu;

    private String idCoAo;

    private String idDanhMuc;

    private String idHoaTiet;

    private String idMauSac;

    private String idKichCo;

    private String idKieuDang;

    private String idTayAo;

    private String idThuongHieu;

    private String idTinhNang;

    private String idSanPham;
}
