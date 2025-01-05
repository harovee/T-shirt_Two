package com.shop.server.infrastructure.security.oauth2.session;

import com.shop.server.infrastructure.constants.auth.Session;
import com.shop.server.infrastructure.security.model.response.InfoUserTShirtTwoResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InfoUserTShirtImpl implements InfoUserTShirt {

    private final HttpSession httpSession;

    @Override
    public String getId() {
        return httpSession.getAttribute(Session.CURRENT_USER_ID).toString();
    }

    @Override
    public String getUserName() {
        return httpSession.getAttribute(Session.CURRENT_USER_NAME).toString();
    }

    @Override
    public String getEmail() {
        return httpSession.getAttribute(Session.CURRENT_USER_EMAIL).toString();
    }

    @Override
    public String getSubscriptionType() {
        return httpSession.getAttribute(Session.CURRENT_USER_SUBSCRIPTION_TYPE).toString();
    }

    @Override
    public String getProfilePicture() {
        return httpSession.getAttribute(Session.CURRENT_USER_PROFILE_PICTURE).toString();
    }

    @Override
    public String getRoleCode() {
        return httpSession.getAttribute(Session.CURRENT_USER_ROLE_CODE).toString();
    }

    @Override
    public String getRoleName() {
        return httpSession.getAttribute(Session.CURRENT_USER_ROLE_NAME).toString();
    }

    @Override
    public String getHost() {
        return httpSession.getAttribute(Session.CURRENT_HOST).toString();
    }

    @Override
    public InfoUserTShirtTwoResponse getInfoUserSpotify() {
        return new InfoUserTShirtTwoResponse(
                getId(),
                getUserName(),
                getEmail(),
                getSubscriptionType(),
                getProfilePicture(),
                getRoleCode(),
                getRoleName(),
                getHost()
        );
    }
}
