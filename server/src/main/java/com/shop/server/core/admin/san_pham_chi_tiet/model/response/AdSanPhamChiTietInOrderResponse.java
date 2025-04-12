package com.shop.server.core.admin.san_pham_chi_tiet.model.response;

import com.shop.server.core.common.base.BaseResponse;

public interface AdSanPhamChiTietInOrderResponse extends BaseResponse {
    String getIdSanPhamGiamGia();

    String getMaSanPhamChiTiet ();

    String getSanPham ();

    String getThuongHieu ();

    String getChatLieu ();

    String getCoAo ();

    String getHoaTiet ();

    String getKieuDang ();

    String getMauSac ();

    String getKichCo ();

    String getTayAo ();

    String getTinhNang ();

    Double getGia ();

    Double getGiaHienTai();

    Double getGiaSauGiam();

    String getImgUrl();

    String getSoLuong ();

    Integer getTrangThai ();
}
