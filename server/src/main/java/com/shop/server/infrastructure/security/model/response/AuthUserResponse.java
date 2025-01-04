package com.shop.server.infrastructure.security.model.response;

public interface AuthUserResponse {

    String fullName();

    String email();

    String businessName();

    String businessTypeId();

    String password();

}
