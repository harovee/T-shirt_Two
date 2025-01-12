package com.shop.server.core.admin.client.models.responses;

import com.shop.server.core.common.base.BaseResponse;

public interface AdminClientResponse extends BaseResponse {

    String getName();

    String getEmail();

    String getCode();

    String getStatus();

}