package com.shop.server.core.client.product.model.response;

import com.shop.server.core.common.base.BaseResponse;

import java.math.BigDecimal;
import java.util.List;

public interface ClientProductDetailResponse extends BaseResponse {

    String getMaSPCT();

    String getAnh();

    String getTen();

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

    Short getSoLuong();

    String getIdTinhNang();

    String getTenTinhNang();

    String getIdKieuDang();

    String getTenKieuDang();

    String getIdThuongHieu();

    String getTenThuongHieu();

    BigDecimal getGia();

    String getSize();

    String getColor();
}
