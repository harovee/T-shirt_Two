package com.shop.server.core.admin.staff.models.responses;

public interface AdminDetailStaffResponse {

    String getId();

    String getCode();

    String getFullName();

    String getBirthday();

    Boolean getGender();

    String getPhoneNumber();

    String getEmail();

    String getPassword();

    String getIdentity();

    Boolean getStatus();

    String getPicture();

    String getCreatedBy();

    String getLastModifiedBy();

    Long getCreatedDate();

    Long getLastModifiedDate();
}