package com.shop.server.infrastructure.security.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shop.server.entities.main.KhachHang;
import com.shop.server.entities.main.NhanVien;
import com.shop.server.infrastructure.constants.auth.Session;
import com.shop.server.infrastructure.constants.module.Role;
import com.shop.server.infrastructure.security.model.response.InfoUserTShirtTwoResponse;
import com.shop.server.infrastructure.security.oauth2.user.UserPrincipal;
import com.shop.server.infrastructure.security.repository.SecurityClientRepository;
import com.shop.server.infrastructure.security.repository.SecurityStaffRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpSession;
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

    private final long TOKEN_EXP = 10 * 60 * 60 * 1000;

    @Setter(onMethod_ = @Autowired)
    private SecurityStaffRepository userAuthStaffRepository;

    @Setter(onMethod_ = @Autowired)
    private SecurityClientRepository userAuthClientRepository;

    @Setter(onMethod_ = @Autowired)
    private HttpSession httpSession;

    public String createToken(Authentication authentication) throws BadRequestException, JsonProcessingException {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        if (userAuthStaffRepository.existsNhanVienById(userPrincipal.getId())) {
            NhanVien nhanVien = getCurrentUserStaffLogin(userPrincipal.getEmail());

            if (nhanVien == null) throw new BadRequestException("user staff not found");

            InfoUserTShirtTwoResponse infoUserTShirtTwoResponse = getInfoUserSpotifyResponse(nhanVien);
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
        } else if (userAuthClientRepository.existsKhachHangById(userPrincipal.getId())) {
            KhachHang khachHang = getCurrentUserClientLogin(userPrincipal.getEmail());

            if (khachHang == null) throw new BadRequestException("user client not found");

            InfoUserTShirtTwoResponse infoUserTShirtTwoResponse = getInfoUserSpotifyResponse(khachHang);
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
        } else {
            throw new BadRequestException("user client not found");
        }
    }

    public String createToken(String userId) throws BadRequestException, JsonProcessingException {
        if (userAuthStaffRepository.existsNhanVienById(userId)) {
            NhanVien nhanVien = userAuthStaffRepository.findById(userId).orElse(null);
            if (nhanVien == null) throw new BadRequestException("user staff not found");

            InfoUserTShirtTwoResponse response = getInfoUserSpotifyResponse(nhanVien);
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
        } else if (userAuthClientRepository.existsKhachHangById(userId)) {
            KhachHang khachHang = userAuthClientRepository.findById(userId).orElse(null);
            if (khachHang == null) throw new BadRequestException("user staff not found");

            InfoUserTShirtTwoResponse response = getInfoUserSpotifyResponse(khachHang);
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
        } else {
            throw new BadRequestException("user client not found");
        }
    }

    private InfoUserTShirtTwoResponse getInfoUserSpotifyResponse(NhanVien nhanVien) {
        InfoUserTShirtTwoResponse response = new InfoUserTShirtTwoResponse();
        response.setId(nhanVien.getId());
        response.setUserName(nhanVien.getFullName());
        response.setEmail(nhanVien.getEmail());
        response.setSubscriptionType(nhanVien.getSubscriptionType());
        response.setProfilePicture(nhanVien.getProfilePicture());
        response.setRoleCode(nhanVien.getRole().name());
        response.setRoleName(nhanVien.getRole().name());
        return response;
    }

    private InfoUserTShirtTwoResponse getInfoUserSpotifyResponse(KhachHang khachHang) {
        InfoUserTShirtTwoResponse response = new InfoUserTShirtTwoResponse();
        response.setId(khachHang.getId());
        response.setUserName(khachHang.getFullName());
        response.setEmail(khachHang.getEmail());
        response.setSubscriptionType(khachHang.getSubscriptionType());
        response.setProfilePicture(khachHang.getProfilePicture());
        response.setRoleCode(Role.CLIENT.name());
        response.setRoleName(Role.CLIENT.name());
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

    public String getRoleFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(tokenSecret.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody();
        String roleCode = claims.get("roleCode", String.class);
        if (roleCode != null && !roleCode.isEmpty()) {
            return roleCode;
        }
        return claims.get("email", String.class);
    }

    public boolean isStaff(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(tokenSecret.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody();
        String roleCode = claims.get("roleCode", String.class);
        if (roleCode != null && !roleCode.isEmpty()) {
            return roleCode.equalsIgnoreCase(Role.ADMIN.name()) || roleCode.equalsIgnoreCase(Role.USER.name());
        }
        return false;
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

    private NhanVien getCurrentUserStaffLogin(String email) {
        Optional<NhanVien> user = userAuthStaffRepository.findByEmail(email);
        return user.orElse(null);
    }

    private KhachHang getCurrentUserClientLogin(String email) {
        Optional<KhachHang> user = userAuthClientRepository.findByEmail(email);
        return user.orElse(null);
    }

    public void setAttributeSession(String authToken) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(tokenSecret.getBytes()))
                .build()
                .parseClaimsJws(authToken)
                .getBody();
        String id = claims.get("id", String.class);
        String userName = claims.get("userName", String.class);
        String email = claims.get("email", String.class);
        String subscriptionType = claims.get("subscriptionType", String.class);
        String profilePicture = claims.get("profilePicture", String.class);
        String roleCode = claims.get("roleCode", String.class);
        String roleName = claims.get("roleName", String.class);
        httpSession.setAttribute(Session.CURRENT_USER_ID, id);
        httpSession.setAttribute(Session.CURRENT_USER_NAME, userName);
        httpSession.setAttribute(Session.CURRENT_USER_EMAIL, email);
        httpSession.setAttribute(Session.CURRENT_USER_SUBSCRIPTION_TYPE, subscriptionType);
        httpSession.setAttribute(Session.CURRENT_USER_PROFILE_PICTURE, profilePicture);
        httpSession.setAttribute(Session.CURRENT_USER_ROLE_CODE, roleCode);
        httpSession.setAttribute(Session.CURRENT_USER_ROLE_NAME, roleName);
    }

}
