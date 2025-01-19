package com.shop.server.core.admin.danh_muc.model.response;

import com.shop.server.core.common.base.BaseResponse;

public interface AdDanhMucResponse extends BaseResponse {

    String getTen ();

    String getMaDanhMuc ();

    Long getNgayTao ();
}
