package com.shop.server.core.admin.client.models.responses;

public interface AdminDetailClientResponse {

    String getId();

    String getCode();

    String getFullName();

    String getBirthday();

    Boolean getGender();

    String getPhoneNumber();

    String getEmail();

    String getPassword();

    Boolean getStatus();

    String getPicture();

    String getCreatedBy();

    String getLastModifiedBy();

    Long getCreatedDate();

    Long getLastModifiedDate();

}
