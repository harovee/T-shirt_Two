package com.shop.server.infrastructure.config.database;


import com.shop.server.entities.base.AuditEntity;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

@Configuration
public class AuditorAwareConfig {
    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                Object principal = authentication.getPrincipal();
                if (principal instanceof UserDetails) {
                    return Optional.of(((UserDetails) principal).getUsername());
                }
            }
            return Optional.empty();
        };
    }

    @PrePersist
    public void prePersist(AuditEntity o) {
        o.setNguoiTao(auditorProvider().getCurrentAuditor().orElse(null));
    }

    @PreUpdate
    public void preUpdate(AuditEntity o) {
        o.setNguoiSua(auditorProvider().getCurrentAuditor().orElse(null));
    }
}
