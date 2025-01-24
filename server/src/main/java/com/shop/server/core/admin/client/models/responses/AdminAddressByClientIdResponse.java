package com.shop.server.core.admin.client.models.responses;

public interface AdminAddressByClientIdResponse {

    String getId();

    String getName();

    String getPhoneNumber();

    String getLine();

    String getWard();

    Long getDistrict();

    Long getProvince();

    String getClientId();

    Boolean getIsDefault();

}
