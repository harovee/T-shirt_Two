package com.shop.server.infrastructure.security.model.response;

import com.shop.server.utils.SecurityUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@AllArgsConstructor
@ToString
@Slf4j
public class TokenUriResponse {

    private String accessToken;

    private String refreshToken;

    public String getTokenAuthorizationSimple() {
        String tokenObject = "{" + "\"accessToken\":\"" + accessToken + "\",\"refreshToken\":\"" + refreshToken + "\"}";
        return SecurityUtil.encodeBase64(tokenObject);
    }

    public static String getState(
            String accessToken,
            String refreshToken
    ) {
        TokenUriResponse tokenUriResponse = new TokenUriResponse(accessToken, refreshToken);
        return tokenUriResponse.getTokenAuthorizationSimple();
    }

}
