package com.shop.server.infrastructure.security.oauth2;

import com.shop.server.entities.main.KhachHang;
import com.shop.server.entities.main.NhanVien;
import com.shop.server.infrastructure.constants.module.ActorConstants;
import com.shop.server.infrastructure.constants.module.Role;
import com.shop.server.infrastructure.security.exception.OAuth2AuthenticationProcessingException;
import com.shop.server.infrastructure.security.oauth2.user.OAuth2UserInfo;
import com.shop.server.infrastructure.security.oauth2.user.OAuth2UserInfoFactory;
import com.shop.server.infrastructure.security.oauth2.user.UserPrincipal;
import com.shop.server.infrastructure.security.repository.SecurityClientRepository;
import com.shop.server.infrastructure.security.repository.SecurityStaffRepository;
import com.shop.server.utils.AESPasswordCryptoUtil;
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

    private final SecurityStaffRepository userStaffRepository;

    private final SecurityClientRepository userClientRepository;

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

        Optional<NhanVien> userAuthStaffOptional = userStaffRepository.findByEmail(oAuth2UserInfo.getEmail());
        Optional<KhachHang> userAuthClientOptional = userClientRepository.findByEmail(oAuth2UserInfo.getEmail());

        if (userAuthClientOptional.isPresent()) {
            KhachHang client = userAuthClientOptional.get();
            if (client.getDeleted().equals(true)) {
                throw new OAuth2AuthenticationProcessingException("The specified user is disabled");
            }
            KhachHang khachHangExist = (KhachHang) updateExistingUser(client, oAuth2UserInfo);
            return UserPrincipal.create(khachHangExist, oAuth2User.getAttributes(), Role.CLIENT.name());
        }

        if (userAuthStaffOptional.isPresent()) {
            NhanVien staff = userAuthStaffOptional.get();
            if (staff.getDeleted().equals(true)) {
                throw new OAuth2AuthenticationProcessingException("The specified user is disabled");
            }
            NhanVien nhanVienExist = (NhanVien) updateExistingUser(staff, oAuth2UserInfo);
            return UserPrincipal.create(nhanVienExist, oAuth2User.getAttributes(), nhanVienExist.getRole().name());
        }

        Object newUser = registerNewUser(oAuth2UserRequest, oAuth2UserInfo);
        if (newUser instanceof KhachHang originClient) {
            return UserPrincipal.create(originClient, oAuth2User.getAttributes(), ActorConstants.CLIENT);
        } else {
            throw new OAuth2AuthenticationProcessingException("Invalid Client Fomat");
        }
    }

    private Object registerNewUser(OAuth2UserRequest oAuth2UserRequest, OAuth2UserInfo oAuth2UserInfo) {
        KhachHang client = new KhachHang();
        client.setFullName(oAuth2UserInfo.getName());
        client.setEmail(oAuth2UserInfo.getEmail());
        client.setSubscriptionType(oAuth2UserInfo.getSubscriptionType());
        client.setProfilePicture(oAuth2UserInfo.getImageUrl());
        client.setDeleted(false);
        client.setPassword(null);
        String pass = AESPasswordCryptoUtil.genPassword(8L);
        client.setPassword(pass);
        Long count = userClientRepository.count() + 1;
        String formattedCode = String.format("%09d", count);
        client.setCode(formattedCode);
        client.setDeleted(false);
        return userClientRepository.save(client);
    }

    private Object updateExistingUser(NhanVien existingNhanVien, OAuth2UserInfo oAuth2UserInfo) {
        existingNhanVien.setFullName(oAuth2UserInfo.getName());
        existingNhanVien.setProfilePicture(oAuth2UserInfo.getImageUrl());
        existingNhanVien.setSubscriptionType(oAuth2UserInfo.getSubscriptionType());
        if (existingNhanVien.getDeleted() == null) existingNhanVien.setDeleted(true);
        return userStaffRepository.save(existingNhanVien);
    }

    private Object updateExistingUser(KhachHang existingKhachHang, OAuth2UserInfo oAuth2UserInfo) {
        existingKhachHang.setFullName(oAuth2UserInfo.getName());
        existingKhachHang.setProfilePicture(oAuth2UserInfo.getImageUrl());
        existingKhachHang.setSubscriptionType(oAuth2UserInfo.getSubscriptionType());
        if (existingKhachHang.getDeleted() == null) existingKhachHang.setDeleted(true);
        return userClientRepository.save(existingKhachHang);
    }

}
