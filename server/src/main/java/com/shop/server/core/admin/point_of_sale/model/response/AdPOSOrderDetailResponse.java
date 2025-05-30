package com.shop.server.core.admin.point_of_sale.model.response;

import com.shop.server.core.common.base.BaseResponse;

public interface AdPOSOrderDetailResponse extends BaseResponse {

    String getMaSanPhamChiTiet();

    String getTen();

    String getSoLuong();

    Double getGia();

    Double getGiaGoc();

    Double getGiaHienTai();

    String getTenSanPham();

    String getTenThuongHieu();

    String getGioiTinh();

    String getKichCo();

    String getMaMauSac();

    String getTenMauSac();

    String getPhongCach();

    String getLinkAnh();

}
