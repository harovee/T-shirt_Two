package com.shop.server.infrastructure.security.oauth2;

import com.shop.server.entities.main.NhanVien;
import com.shop.server.infrastructure.constants.module.ActorConstants;
import com.shop.server.infrastructure.constants.module.Role;
import com.shop.server.infrastructure.constants.module.Status;
import com.shop.server.infrastructure.security.exception.OAuth2AuthenticationProcessingException;
import com.shop.server.infrastructure.security.oauth2.user.OAuth2UserInfo;
import com.shop.server.infrastructure.security.oauth2.user.OAuth2UserInfoFactory;
import com.shop.server.infrastructure.security.oauth2.user.UserPrincipal;
import com.shop.server.infrastructure.security.repository.SecurityNhanVienRepository;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final SecurityNhanVienRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);
        try {
            return processOAuth2User(oAuth2UserRequest, oAuth2User);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory
                .getOAuth2UserInfo(
                        oAuth2UserRequest.getClientRegistration().getRegistrationId(),
                        oAuth2User.getAttributes()
                );
        if (oAuth2UserInfo.getEmail() == null || oAuth2UserInfo.getEmail().isBlank()) {
            throw new OAuth2AuthenticationProcessingException("Email not found from OAuth2 provider");
        }

        Optional<NhanVien> userAuthOptional = userRepository.findByEmail(oAuth2UserInfo.getEmail());

        if (userAuthOptional.isPresent()) {
            NhanVien nhanVien = userAuthOptional.get();
            if(nhanVien.getStatus().equals(Status.INACTIVE)){
                throw new OAuth2AuthenticationProcessingException("The specified user is disabled");
            }
            NhanVien nhanVienExist = (NhanVien) updateExistingUser(nhanVien, oAuth2UserInfo);
            return UserPrincipal.create(nhanVienExist, oAuth2User.getAttributes(), nhanVienExist.getRole().name());
        }

        Object newUser = registerNewUser(oAuth2UserRequest, oAuth2UserInfo);
        if (newUser instanceof NhanVien originNhanVien) {
            return UserPrincipal.create(originNhanVien, oAuth2User.getAttributes(), ActorConstants.CLIENT);
        } else {
            throw new OAuth2AuthenticationProcessingException("Invalid email format");
        }
    }

    private Object registerNewUser(OAuth2UserRequest oAuth2UserRequest, OAuth2UserInfo oAuth2UserInfo) {
        NhanVien nhanVien = new NhanVien();
        nhanVien.setFullName(oAuth2UserInfo.getName());
        nhanVien.setEmail(oAuth2UserInfo.getEmail());
        nhanVien.setSubscriptionType(oAuth2UserInfo.getSubscriptionType());
        nhanVien.setProfilePicture(oAuth2UserInfo.getImageUrl());
        nhanVien.setStatus(Status.ACTIVE);
        nhanVien.setRole(Role.CLIENT);
        nhanVien.setPassword(null);
        return userRepository.save(nhanVien);
    }

    private Object updateExistingUser(NhanVien existingNhanVien, OAuth2UserInfo oAuth2UserInfo) {
        existingNhanVien.setFullName(oAuth2UserInfo.getName());
        existingNhanVien.setProfilePicture(oAuth2UserInfo.getImageUrl());
        existingNhanVien.setSubscriptionType(oAuth2UserInfo.getSubscriptionType());
        if (existingNhanVien.getStatus() == null) existingNhanVien.setStatus(Status.ACTIVE);
        return userRepository.save(existingNhanVien);
    }

}
