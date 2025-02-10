package com.shop.server.core.admin.point_of_sale.model.request;

import com.shop.server.core.common.base.PageableRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AdPOSFindProductRequest extends PageableRequest {

    private String keyword; // ma, sp ,ten spct, ten mau

    private Boolean gioiTinh;

    private String idThuongHieu;
    private String idKichCo;
    private String idCoAo;
    private String idTayAo;
    private String idHoaTiet;
    private String idChatLieu;
    private String idKieuDang;
    private String idTinhNang;

    private Double bienGiaBe = 0D;

    private Double bienGiaLon;

}
