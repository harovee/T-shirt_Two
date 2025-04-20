package com.shop.server.core.client.product.model.response;

import com.shop.server.core.common.base.BaseResponse;

public interface ClientProductProjectionResponse extends BaseResponse {
    String getMaSanPham();

    String getTen();

    String getMaSPCTs();

    String getMoTa();

    String getIdDanhMuc();

    String getTenDanhMuc();

    String getIdChatLieu();

    String getTenChatLieu();

    String getIdTayAo();

    String getTenTayAo();

    String getIdCoAo();

    String getTenCoAo();

    String getIdHoaTiet();

    String getTenHoaTiet();

    String getIdTinhNang();

    String getTenTinhNang();

    String getIdKieuDang();

    String getTenKieuDang();

    String getIdThuongHieu();

    String getTenThuongHieu();

    String getdiscount();

    String getGia();

    String getKichCos();

    String getColors();

    String getAnhs();

    String getPhanTramGiam();

    Integer getTongSoLuongBan();
}
