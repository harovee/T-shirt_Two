package com.shop.server.infrastructure.security.service.impl;

import com.shop.server.core.common.base.ResponseObject;
import com.shop.server.entities.main.NhanVien;
import com.shop.server.entities.main.RefreshToken;
import com.shop.server.infrastructure.security.model.request.AuthLoginRequest;
import com.shop.server.infrastructure.security.model.request.AuthRefreshRequest;
import com.shop.server.infrastructure.security.model.request.AuthRegisterRequest;
import com.shop.server.infrastructure.security.model.response.AuthRefreshResponse;
import com.shop.server.infrastructure.security.model.response.TokenUriResponse;
import com.shop.server.infrastructure.security.repository.SecurityNhanVienRepository;
import com.shop.server.infrastructure.security.repository.SecurityRefreshRepository;
import com.shop.server.infrastructure.security.service.RefreshTokenService;
import com.shop.server.infrastructure.security.service.SecurityRefreshTokenService;
import com.shop.server.infrastructure.security.service.TokenProvider;
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

    private final SecurityNhanVienRepository authStaffRepository;

    private final RefreshTokenService refreshTokenService;

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
            Optional<NhanVien> nhanVienOptional = authStaffRepository.findByUsername(request.getUserName());
            if (nhanVienOptional.isPresent()) {
                NhanVien staff = nhanVienOptional.get();
                if (staff.getPassword().equals(request.getPassword())) {
                    String accessToken = tokenProvider.createToken(staff.getId());
                    String refreshToken = refreshTokenService.createRefreshToken(staff.getId()).getRefreshToken();
                    return ResponseObject.successForward(TokenUriResponse.getState(accessToken, refreshToken), "Get state successfully");
                } else {
                    return ResponseObject.errorForward(HttpStatus.BAD_REQUEST, "Incorrect password");
                }
            }
            return ResponseObject.errorForward(HttpStatus.BAD_REQUEST, "User does not exits");
        } catch (Exception e) {
            log.info("😢😢 ~> Error login");
            return ResponseObject.errorForward(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public ResponseObject<?> register(AuthRegisterRequest request) {
        try {
//            Optional<KhachHang> userOptional = authStaffRepository.findByEmail(request.getEmail());
//            if (userOptional.isPresent()) {
//                return ResponseObject.errorForward(HttpStatus.BAD_REQUEST, "Email already in use");
//            }
//            NhanVien nhanVien = new NhanVien();
//            nhanVien.setEmail(request.getEmail());
//            nhanVien.setPassword(request.getPassword());
//            nhanVien.setRole(Role.CLIENT);
//            nhanVien.setStatus(Status.ACTIVE);
//            String userId = authStaffRepository.save(nhanVien).getId();
//            String accessToken = tokenProvider.createToken(userId);
//            String refreshToken = refreshTokenService.createRefreshToken(userId).getRefreshToken();
//            return ResponseObject.successForward(TokenUriResponse.getState(accessToken, refreshToken), "Get state successfully");
            return null;
        } catch (Exception e) {
            log.info("😢😢 ~> Error encrypt register");
            return ResponseObject.errorForward(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

}
