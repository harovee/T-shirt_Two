package com.shop.server.core.admin.anh_san_pham.model.response;

import com.shop.server.core.common.base.BaseResponse;

public interface AdAnhResponse extends BaseResponse {

    String getTen ();

    String getUrl ();

    String getSanPhamChiTiet ();
}
