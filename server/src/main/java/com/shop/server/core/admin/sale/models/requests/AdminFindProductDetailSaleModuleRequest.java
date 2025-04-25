package com.shop.server.core.admin.sale.models.requests;

import com.shop.server.core.common.base.PageableRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AdminFindProductDetailSaleModuleRequest extends PageableRequest {

    private List<String> idSanPhams;

    private String keyword; // ma, sp ,ten spct, ten mau

    private String gioiTinh;

    private String idThuongHieu;
    private String idKichCo;
    private String idCoAo;
    private String idTayAo;
    private String idHoaTiet;
    private String idChatLieu;
    private String idKieuDang;
    private String idTinhNang;


}
