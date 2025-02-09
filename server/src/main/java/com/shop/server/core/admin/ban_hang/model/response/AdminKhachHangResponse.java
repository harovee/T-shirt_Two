package com.shop.server.core.admin.ban_hang.model.response;

import com.shop.server.core.common.base.BaseResponse;

public interface AdminKhachHangResponse extends BaseResponse {

    String getProfilePicture();

    String getName();

    String getEmail();

    String getPhoneNumber();

    String getTinh();

    String getHuyen();

    String getXa();

    String getSoNha();
}
