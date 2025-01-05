package com.shop.server.infrastructure.security.oauth2.session;

import com.shop.server.infrastructure.security.model.response.InfoUserTShirtTwoResponse;

public interface InfoUserTShirt {

    String getId();

    String getUserName();

    String getEmail();

    String getSubscriptionType();

    String getProfilePicture();

    String getRoleCode();

    String getRoleName();

    String getHost();

    InfoUserTShirtTwoResponse getInfoUserSpotify();

}
