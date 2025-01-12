package com.shop.server.infrastructure.security.oauth2.session;

import com.shop.server.infrastructure.constants.auth.Session;
import com.shop.server.infrastructure.security.model.response.InfoUserTShirtTwoResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class InfoUserTShirtImpl implements InfoUserTShirt {

    private final HttpSession httpSession;

    public InfoUserTShirtImpl(HttpSession httpSession) {
        this.httpSession = httpSession;
    }

    @Override
    public String getId() {
        log.info("Session.CURRENT_USER_ID + {}", httpSession.getAttribute(Session.CURRENT_USER_ID).toString());
        return httpSession.getAttribute(Session.CURRENT_USER_ID).toString();
    }

    @Override
    public String getUserName() {
        log.info("Session.CURRENT_USER_NAME + {}", httpSession.getAttribute(Session.CURRENT_USER_NAME).toString());
        return httpSession.getAttribute(Session.CURRENT_USER_NAME).toString();
    }

    @Override
    public String getEmail() {
        log.info("Session.CURRENT_USER_EMAIL + {}", httpSession.getAttribute(Session.CURRENT_USER_EMAIL).toString());
        return httpSession.getAttribute(Session.CURRENT_USER_EMAIL).toString();
    }

    @Override
    public String getSubscriptionType() {
        log.info("Session.CURRENT_USER_SUBSCRIPTION_TYPE + {}", httpSession.getAttribute(Session.CURRENT_USER_SUBSCRIPTION_TYPE).toString());
        return httpSession.getAttribute(Session.CURRENT_USER_SUBSCRIPTION_TYPE).toString();
    }

    @Override
    public String getProfilePicture() {
        log.info("Session.CURRENT_USER_PROFILE_PICTURE + {}", httpSession.getAttribute(Session.CURRENT_USER_PROFILE_PICTURE).toString());
        return httpSession.getAttribute(Session.CURRENT_USER_PROFILE_PICTURE).toString();
    }

    @Override
    public String getRoleCode() {
        log.info("Session.CURRENT_USER_ROLE_CODE + {}", httpSession.getAttribute(Session.CURRENT_USER_ROLE_CODE).toString());
        return httpSession.getAttribute(Session.CURRENT_USER_ROLE_CODE).toString();
    }

    @Override
    public String getRoleName() {
        log.info("Session.CURRENT_USER_ROLE_NAME + {}", httpSession.getAttribute(Session.CURRENT_USER_ROLE_NAME).toString());
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
