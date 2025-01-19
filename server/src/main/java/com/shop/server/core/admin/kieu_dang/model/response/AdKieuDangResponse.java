package com.shop.server.core.admin.kieu_dang.model.response;

import com.shop.server.core.common.base.BaseResponse;

public interface AdKieuDangResponse extends BaseResponse {

    String getTen ();

    String getMaKieuDang ();

    Long getNgayTao ();
}
