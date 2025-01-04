package com.shop.server.infrastructure.security.oauth2;

import com.shop.server.entities.Staff;
import com.shop.server.infrastructure.constants.module.ActorConstants;
import com.shop.server.infrastructure.constants.module.Role;
import com.shop.server.infrastructure.constants.module.Status;
import com.shop.server.infrastructure.security.exception.OAuth2AuthenticationProcessingException;
import com.shop.server.infrastructure.security.oauth2.user.OAuth2UserInfo;
import com.shop.server.infrastructure.security.oauth2.user.OAuth2UserInfoFactory;
import com.shop.server.infrastructure.security.oauth2.user.UserPrincipal;
import com.shop.server.infrastructure.security.repository.SecurityUserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final SecurityUserRepository userRepository;

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

        Optional<Staff> userAuthOptional = userRepository.findByEmail(oAuth2UserInfo.getEmail());

        if (userAuthOptional.isPresent()) {
            Staff staff = userAuthOptional.get();
            if(staff.getStatus().equals(Status.INACTIVE)){
                throw new OAuth2AuthenticationProcessingException("The specified user is disabled");
            }
            Staff staffExist = (Staff) updateExistingUser(staff, oAuth2UserInfo);
            return UserPrincipal.create(staffExist, oAuth2User.getAttributes(), staffExist.getRole().name());
        }

        Object newUser = registerNewUser(oAuth2UserRequest, oAuth2UserInfo);
        if (newUser instanceof Staff originStaff) {
            return UserPrincipal.create(originStaff, oAuth2User.getAttributes(), ActorConstants.USER);
        } else {
            throw new OAuth2AuthenticationProcessingException("Invalid email format");
        }
    }

    private Object registerNewUser(OAuth2UserRequest oAuth2UserRequest, OAuth2UserInfo oAuth2UserInfo) {
        Staff staff = new Staff();
        staff.setUserName(oAuth2UserInfo.getName());
        staff.setEmail(oAuth2UserInfo.getEmail());
        staff.setSubscriptionType(oAuth2UserInfo.getSubscriptionType());
        staff.setProfilePicture(oAuth2UserInfo.getImageUrl());
        staff.setStatus(Status.ACTIVE);
        staff.setRole(Role.USER);
        staff.setPassword(null);
        return userRepository.save(staff);
    }

    private Object updateExistingUser(Staff existingStaff, OAuth2UserInfo oAuth2UserInfo) {
        existingStaff.setUserName(oAuth2UserInfo.getName());
        existingStaff.setProfilePicture(oAuth2UserInfo.getImageUrl());
        existingStaff.setSubscriptionType(oAuth2UserInfo.getSubscriptionType());
        if (existingStaff.getStatus() == null) existingStaff.setStatus(Status.ACTIVE);
        return userRepository.save(existingStaff);
    }

}
