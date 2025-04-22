package com.shop.server.core.admin.san_pham_chi_tiet.model.request;

import com.shop.server.core.admin.image.model.request.AdCreateUpdateAnhRequest;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
public class AdCreateUpdateSpctRequest {

    private BigDecimal gia;

    private Long soLuong;

    private Integer trangThai;

    private String idChatLieu;

    private String idCoAo;

    private String idHoaTiet;

    private String idMauSac;

    private String idKichCo;

    private String idKieuDang;

    private String idTayAo;

    private String idThuongHieu;

    private String idTinhNang;

    private String idSanPham;

    private String gioiTinh;

    private List<AdCreateUpdateAnhRequest> listAnh;
}
