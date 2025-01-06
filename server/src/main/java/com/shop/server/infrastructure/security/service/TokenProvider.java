package com.shop.server.infrastructure.security.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shop.server.entities.main.Staff;
import com.shop.server.infrastructure.constants.auth.Session;
import com.shop.server.infrastructure.security.model.response.InfoUserTShirtTwoResponse;
import com.shop.server.infrastructure.security.oauth2.user.UserPrincipal;
import com.shop.server.infrastructure.security.repository.SecurityNhanVienRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class TokenProvider {

    @Value("${jwt.secret}")
    private String tokenSecret;

    private final long TOKEN_EXP = 2 * 60 * 60 * 1000;

    @Setter(onMethod_ = @Autowired)
    private SecurityNhanVienRepository userAuthRepository;

    public String createToken(Authentication authentication) throws BadRequestException, JsonProcessingException {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Staff staff = getCurrentUserLogin(userPrincipal.getEmail());

        if (staff == null) throw new BadRequestException("user not found");

        InfoUserTShirtTwoResponse infoUserTShirtTwoResponse = getInfoUserSpotifyResponse(staff);
        String subject = new ObjectMapper().writeValueAsString(infoUserTShirtTwoResponse);
        Map<String, Object> claims = getBodyClaims(infoUserTShirtTwoResponse);

        return Jwts.builder()
                .setSubject(subject)
                .setClaims(claims)
                .setIssuedAt(new java.util.Date(System.currentTimeMillis()))
                .setExpiration(new java.util.Date(System.currentTimeMillis() + TOKEN_EXP))
                .setIssuer("T-Shirt Two")
                .signWith(Keys.hmacShaKeyFor(tokenSecret.getBytes()))
                .compact();
    }

    public String createToken(String userId) throws BadRequestException, JsonProcessingException {
        Staff staff = userAuthRepository.findById(userId).orElse(null);
        if (staff == null) throw new BadRequestException("user not found");

        InfoUserTShirtTwoResponse response = getInfoUserSpotifyResponse(staff);
        String subject = new ObjectMapper().writeValueAsString(response);
        Map<String, Object> claims = getBodyClaims(response);

        return Jwts.builder()
                .setSubject(subject)
                .setClaims(claims)
                .setIssuedAt(new java.util.Date(System.currentTimeMillis()))
                .setExpiration(new java.util.Date(System.currentTimeMillis() + TOKEN_EXP))
                .setIssuer("T-Shirt Two")
                .signWith(Keys.hmacShaKeyFor(tokenSecret.getBytes()))
                .compact();
    }

    private InfoUserTShirtTwoResponse getInfoUserSpotifyResponse(Staff staff) {
        InfoUserTShirtTwoResponse response = new InfoUserTShirtTwoResponse();
        response.setId(staff.getId());
        response.setUserName(staff.getUserName());
        response.setEmail(staff.getEmail());
        response.setSubscriptionType(staff.getSubscriptionType());
        response.setProfilePicture(staff.getProfilePicture());
        response.setRoleCode(staff.getRole().name());
        response.setRoleName(staff.getRole().name());
        return response;
    }

    private static Map<String, Object> getBodyClaims(InfoUserTShirtTwoResponse response) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(Session.CURRENT_USER_ID, response.getId());
        claims.put(Session.CURRENT_USER_NAME, response.getUserName());
        claims.put(Session.CURRENT_USER_EMAIL, response.getEmail());
        claims.put(Session.CURRENT_USER_SUBSCRIPTION_TYPE, response.getSubscriptionType());
        claims.put(Session.CURRENT_USER_PROFILE_PICTURE, response.getProfilePicture());
        claims.put(Session.CURRENT_USER_ROLE_CODE, response.getRoleCode());
        claims.put(Session.CURRENT_USER_ROLE_NAME, response.getRoleName());
        claims.put(Session.CURRENT_HOST, response.getHost());
        return claims;
    }

    public String getUserIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(tokenSecret.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody();
        return String.valueOf(claims.get("userId").toString());
    }

    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(tokenSecret.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody();
        String email = claims.get("email", String.class);
        if (email != null && !email.isEmpty()) {
            return email;
        }
        return claims.get("email", String.class);
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(tokenSecret.getBytes()))
                    .build()
                    .parseClaimsJws(authToken);
            return true;
        } catch (SignatureException ex) {
            log.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty.");
        }
        return false;
    }

    private Staff getCurrentUserLogin(String email) {
        Optional<Staff> user = userAuthRepository.findByEmail(email);
        return user.orElse(null);
    }

}
