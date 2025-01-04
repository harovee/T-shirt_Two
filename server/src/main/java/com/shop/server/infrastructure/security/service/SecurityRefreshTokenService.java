package com.shop.server.infrastructure.security.service;

import com.shop.server.core.common.base.ResponseObject;
import com.shop.server.infrastructure.security.model.request.AuthLoginRequest;
import com.shop.server.infrastructure.security.model.request.AuthRefreshRequest;
import com.shop.server.infrastructure.security.model.request.AuthRegisterRequest;
import jakarta.validation.Valid;

public interface SecurityRefreshTokenService {

    ResponseObject<?> getRefreshToken(@Valid AuthRefreshRequest request);

    ResponseObject<?> logout(@Valid AuthRefreshRequest request);

    ResponseObject<?> login(@Valid AuthLoginRequest request);

    ResponseObject<?> register(@Valid AuthRegisterRequest request);

}
