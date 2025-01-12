package com.shop.server.core.admin.product.models.responses;

import com.shop.server.core.common.base.BaseResponse;

public interface AdSanPhamChiTietResponse extends BaseResponse {

    String getMaSanPhamChiTiet ();

    String getDanhMuc ();

    String getThuongHieu ();

    String getChatLieu ();

    String getCoAo ();

    String getHoaTiet ();

    String getKieuDang ();

    String getMauSac ();

    String getKichCo ();

    String getTayAo ();

    String getTinhNang ();

    String getTen ();

    String getGia ();

    String getSoLuong ();

    Integer getTrangThai ();
}
