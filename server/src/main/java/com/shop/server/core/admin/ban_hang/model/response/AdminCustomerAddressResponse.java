package com.shop.server.core.admin.ban_hang.model.response;

import com.shop.server.core.common.base.BaseResponse;

public interface AdminCustomerAddressResponse extends BaseResponse {

    String getName();

    String getPhoneNumber();

    String getLine();

    String getWard();

    String getDistrict();

    String getProvince();

    String getClientId();

    Boolean getIsDefault();

}
