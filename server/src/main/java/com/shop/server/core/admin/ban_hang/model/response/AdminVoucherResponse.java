package com.shop.server.core.admin.ban_hang.model.response;

import com.shop.server.core.common.base.BaseResponse;

public interface AdminVoucherResponse extends BaseResponse {

    String getMa();

    String getTen();

    Double getGiaTriGiam();

    Boolean getKieu();

    Integer getSoLuong();

    Double getDieuKienGiam();

    Boolean getLoaiGiam();

    Long getNgayBatDau();

    Long getNgayKetThuc();

}
