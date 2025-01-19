package com.shop.server.core.admin.hoa_tiet.model.response;

import com.shop.server.core.common.base.BaseResponse;

public interface AdHoaTietResponse extends BaseResponse {

    String getTen ();

    String getMaHoaTiet ();

    Long getNgayTao ();
}
