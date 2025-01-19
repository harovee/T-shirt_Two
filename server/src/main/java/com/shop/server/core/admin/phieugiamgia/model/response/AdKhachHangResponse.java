package com.shop.server.core.admin.phieugiamgia.model.response;

import com.shop.server.core.common.base.BaseResponse;

public interface AdKhachHangResponse extends BaseResponse {
    String getName();

    String getEmail();

    String getPhone();
}
