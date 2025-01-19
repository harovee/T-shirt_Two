package com.shop.server.core.admin.tinh_nang.model.response;

import com.shop.server.core.common.base.BaseResponse;

public interface AdTinhNangResponse extends BaseResponse {

    String getTen ();

    String getMaTinhNang ();

    Long getNgayTao ();
}
