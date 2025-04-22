package com.shop.server.core.admin.san_pham_chi_tiet.model.response;

import com.shop.server.core.common.base.BaseResponse;
import com.shop.server.entities.main.*;

public interface AdSanPhamChiTietResponse extends BaseResponse {

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

    String getGia ();

    String getSoLuong ();

    Integer getTrangThai ();

    String getGioiTinh();
}
