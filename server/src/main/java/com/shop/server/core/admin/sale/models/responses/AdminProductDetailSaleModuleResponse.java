package com.shop.server.core.admin.sale.models.responses;

import com.shop.server.core.common.base.BaseResponse;

public interface AdminProductDetailSaleModuleResponse extends BaseResponse {

    String getMaSanPhamChiTiet();

    String getTen();

    String getSoLuong();

    String getGia();

    String getTenSanPham();

    String getTenThuongHieu();

    Boolean getGioiTinh();

    String getKichCo();

    String getPhongCach();

    String getLinkAnh();

}
