package com.shop.server.infrastructure.security.oauth2;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.shop.server.infrastructure.security.exception.RedirectException;
import com.shop.server.infrastructure.security.model.response.TokenUriResponse;
import com.shop.server.infrastructure.security.service.RefreshTokenService;
import com.shop.server.infrastructure.security.service.TokenProvider;
import com.shop.server.utils.CookieUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Setter;

import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.Optional;

import static com.shop.server.infrastructure.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Setter(onMethod_ = @Autowired)
    private TokenProvider tokenProvider;

    @Setter(onMethod_ = @Autowired)
    private RefreshTokenService refreshTokenService;

    @Setter(onMethod_ = @Autowired)
    private HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

    @Value("${frontend.url}")
    private String DEFAULT_TARGET_URL_TUTOR_CLIENT;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {
        String targetUrl = determineTargetUrl(request, response, authentication);
        if (response.isCommitted()) {
            logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }
        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        try {
            Optional<String> redirectUri = CookieUtils.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
                    .map(Cookie::getValue);
            if (redirectUri.isEmpty()) throw new RedirectException("Redirect uri not found! Please try again later!");
            String targetUrl = redirectUri.orElse(getDefaultTargetUrl());
            String token = tokenProvider.createToken(authentication);
            String refreshToken = refreshTokenService.createRefreshToken(authentication).getRefreshToken();
            return buildSuccessUrl(targetUrl, TokenUriResponse.getState(token, refreshToken));
        } catch (RedirectException e) {
            e.printStackTrace(System.out);
            return buildErrorUri(e.getMessage());
        } catch (BadRequestException | JsonProcessingException | MalformedURLException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }

    private String buildErrorUri(String errorMessage) {
        return DEFAULT_TARGET_URL_TUTOR_CLIENT + "/error?error=" + errorMessage;
    }

    private String buildSuccessUrl(String targetUrl, String token) throws MalformedURLException, URISyntaxException {
        return targetUrl + "?state=" + token;
    }

}
