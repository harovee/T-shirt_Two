package com.shop.server.core.admin.employee.models.responses;

import com.shop.server.core.common.base.BaseResponse;

public interface AdminEmployeeResponse extends BaseResponse {

    String getName();

    String getEmail();

    String getStatus();

}