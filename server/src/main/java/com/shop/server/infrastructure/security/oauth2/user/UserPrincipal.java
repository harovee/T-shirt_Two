package com.shop.server.infrastructure.security.oauth2.user;

import com.shop.server.entities.Staff;
import com.shop.server.infrastructure.constants.module.ActorConstants;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
public class UserPrincipal implements OAuth2User, UserDetails {

    @Getter
    private final String id;

    @Getter
    private final String email;

    private final Collection<? extends GrantedAuthority> authorities;

    private String password;

    @Setter
    private Map<String, Object> attributes;

    public UserPrincipal(String id, String email, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.email = email;
        this.authorities = authorities;
    }

    public static UserPrincipal create(Staff staff) {
        List<GrantedAuthority> authorities = Collections.
                singletonList(new SimpleGrantedAuthority(ActorConstants.ADMIN));

        return new UserPrincipal(
                staff.getId(),
                staff.getEmail(),
                authorities
        );
    }

    public static UserPrincipal create(Staff staff, String role) {
        List<GrantedAuthority> authorities = Collections.
                singletonList(new SimpleGrantedAuthority(role));

        return new UserPrincipal(
                staff.getId(),
                staff.getEmail(),
                authorities
        );
    }

    public static UserPrincipal create(Staff staff, Map<String, Object> attributes, String roles) {
        UserPrincipal userPrincipal = UserPrincipal.create(staff, roles);
        userPrincipal.setAttributes(attributes);
        return userPrincipal;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        return String.valueOf(id);
    }

}
