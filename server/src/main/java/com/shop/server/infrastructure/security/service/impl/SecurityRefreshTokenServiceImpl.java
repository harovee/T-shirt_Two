package com.shop.server.infrastructure.security.service.impl;

import com.shop.server.core.admin.client.repositories.AdminClientRepository;
import com.shop.server.core.common.base.ResponseObject;
import com.shop.server.entities.main.KhachHang;
import com.shop.server.entities.main.RefreshToken;
import com.shop.server.infrastructure.security.model.request.AuthLoginRequest;
import com.shop.server.infrastructure.security.model.request.AuthRefreshRequest;
import com.shop.server.infrastructure.security.model.request.AuthRegisterRequest;
import com.shop.server.infrastructure.security.model.response.AuthRefreshResponse;
import com.shop.server.infrastructure.security.model.response.TokenUriResponse;
import com.shop.server.infrastructure.security.repository.SecurityClientRepository;
import com.shop.server.infrastructure.security.repository.SecurityRefreshRepository;
import com.shop.server.infrastructure.security.repository.SecurityStaffRepository;
import com.shop.server.infrastructure.security.service.RefreshTokenService;
import com.shop.server.infrastructure.security.service.SecurityRefreshTokenService;
import com.shop.server.infrastructure.security.service.TokenProvider;
import com.shop.server.utils.DefaultImageUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Validated
@Slf4j
public class SecurityRefreshTokenServiceImpl implements SecurityRefreshTokenService {

    private final TokenProvider tokenProvider;

    private final SecurityRefreshRepository authRefreshTokenRepository;

    private final SecurityStaffRepository authStaffRepository;

    private final SecurityClientRepository authClientRepository;

    private final RefreshTokenService refreshTokenService;
    private final AdminClientRepository adminClientRepository;

    @Override
    public ResponseObject<?> getRefreshToken(@Valid AuthRefreshRequest request) {
        try {
            String refreshToken = request.getRefreshToken();

            Optional<RefreshToken> refreshTokenOptional = authRefreshTokenRepository.findByRefreshToken(refreshToken);
            if (refreshTokenOptional.isEmpty()) {
                return ResponseObject.errorForward(HttpStatus.NOT_FOUND, "Refresh token not found");
            }

            RefreshToken refreshTokenEntity = refreshTokenOptional.get();
            if (refreshTokenEntity.getRevokedAt() != null) {
                return ResponseObject.errorForward(HttpStatus.BAD_REQUEST, "Refresh token has been revoked");
            }

            String accessToken = tokenProvider.createToken(refreshTokenEntity.getUserId());
            return ResponseObject.successForward(new AuthRefreshResponse(accessToken, refreshToken), "Get refresh token successfully");
        } catch (Exception e) {
            return ResponseObject.errorForward(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
        }
    }

    @Override
    public ResponseObject<?> logout(@Valid AuthRefreshRequest request) {
        String refreshToken = request.getRefreshToken();

        Optional<RefreshToken> refreshTokenOptional = authRefreshTokenRepository.findByRefreshToken(refreshToken);
        if (refreshTokenOptional.isEmpty()) {
            return ResponseObject.errorForward(HttpStatus.NOT_FOUND, "Refresh token not found");
        }

        RefreshToken refreshTokenEntity = refreshTokenOptional.get();
        refreshTokenEntity.setRevokedAt(System.currentTimeMillis());
        authRefreshTokenRepository.save(refreshTokenEntity);

        return ResponseObject.successForward(null, "Logout successfully");
    }

    @Override
    public ResponseObject<?> login(AuthLoginRequest request) {
        try {
            Optional<KhachHang> khachHangOptional = authClientRepository.getKhachHangByEmailOrPhoneNumber(request.getEmail(), request.getEmail());
            if (khachHangOptional.isPresent()) {
                KhachHang client = khachHangOptional.get();
                if (client.getPassword().equals(request.getPassword())) {
                    String accessToken = tokenProvider.createToken(client.getId());
                    String refreshToken = refreshTokenService.createRefreshToken(client.getId()).getRefreshToken();
                    return ResponseObject.successForward(TokenUriResponse.getState(accessToken, refreshToken), "Get state successfully");
                } else {
                    return ResponseObject.errorForward(HttpStatus.BAD_REQUEST, "Mật khẩu hoặc tài khoản sai");
                }
            }
            return ResponseObject.errorForward(HttpStatus.BAD_REQUEST, "Tài khoản không tồn tại trong hệ thống");
        } catch (Exception e) {
            e.printStackTrace(System.out);
            return ResponseObject.errorForward(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public ResponseObject<?> register(AuthRegisterRequest request) {
        try {
            Optional<KhachHang> userOptional = authClientRepository.findByEmail(request.getEmail());
            if (userOptional.isPresent()) {
                return ResponseObject.errorForward(HttpStatus.BAD_REQUEST, "Email already in use");
            }
            KhachHang khachHang = new KhachHang();

            khachHang.setFullName(request.getFullName());
            khachHang.setPhoneNumber(request.getPhoneNumber());
            Long count = adminClientRepository.count() + 1;
            String formattedCode = String.format("%09d", count);
            khachHang.setCode(formattedCode);
            khachHang.setProfilePicture(DefaultImageUtil.IMAGE);
            khachHang.setEmail(request.getEmail());
            khachHang.setPassword(request.getPassword());
            khachHang.setDeleted(false);
            String userId = authClientRepository.save(khachHang).getId();
            String accessToken = tokenProvider.createToken(userId);
            String refreshToken = refreshTokenService.createRefreshToken(userId).getRefreshToken();
            return ResponseObject.successForward(TokenUriResponse.getState(accessToken, refreshToken), "Get state successfully");
        } catch (Exception e) {
            e.printStackTrace(System.out);
            log.info("😢😢 ~> Error encrypt register");
            return ResponseObject.errorForward(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

}
