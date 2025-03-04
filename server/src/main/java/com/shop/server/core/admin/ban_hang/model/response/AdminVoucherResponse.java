package com.shop.server.core.admin.ban_hang.model.response;

import com.shop.server.core.common.base.BaseResponse;

public interface AdminVoucherResponse extends BaseResponse {

    String getMa();

    String getTen();

    Short getSoLuong();

    Double getDieuKienGiam();

    Double getGiamToiDa();

    Boolean getLoaiGiam();

    Double getGiaTriGiam();

    Long getNgayBatDau();

    Long getNgayKetThuc();

    Boolean getKieu();

    String getTrangThai();

    String getGiaTri ();

}
