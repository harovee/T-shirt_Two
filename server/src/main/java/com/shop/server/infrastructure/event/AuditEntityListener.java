package com.shop.server.infrastructure.event;

import com.shop.server.entities.base.AuditEntity;
import com.shop.server.infrastructure.security.oauth2.user.UserPrincipal;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Calendar;

public class AuditEntityListener {

    public String getAuditorProviderByAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof UserPrincipal userPrincipal) {
            return userPrincipal.getId();
        }
        return "System";
    }

    @PrePersist
    private void onCreate(AuditEntity entity) {
        entity.setCreatedDate(getCurrentTime());
        entity.setLastModifiedDate(getCurrentTime());
        entity.setNguoiTao(getAuditorProviderByAuthentication());
        entity.setNguoiSua(getAuditorProviderByAuthentication());
    }

    @PreUpdate
    private void onUpdate(AuditEntity entity) {
        entity.setLastModifiedDate(getCurrentTime());
        entity.setNguoiSua(getAuditorProviderByAuthentication());
    }

    private long getCurrentTime() {
        return Calendar.getInstance().getTimeInMillis();
    }

}