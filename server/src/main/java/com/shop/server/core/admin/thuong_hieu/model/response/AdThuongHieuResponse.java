package com.shop.server.core.admin.thuong_hieu.model.response;

import com.shop.server.core.common.base.BaseResponse;

public interface AdThuongHieuResponse extends BaseResponse {

    String getTen ();

    String getMaThuongHieu ();

    Long getNgayTao ();
}
