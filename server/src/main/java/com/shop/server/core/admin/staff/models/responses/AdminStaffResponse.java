package com.shop.server.core.admin.staff.models.responses;

import com.shop.server.core.common.base.BaseResponse;

public interface AdminStaffResponse extends BaseResponse {

    String getName();

    String getEmail();

    String getCode();

    String getPhoneNumber();

    String getStatus();

}