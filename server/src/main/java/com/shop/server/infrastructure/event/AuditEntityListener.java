package com.shop.server.infrastructure.event;

import com.shop.server.entities.base.AuditEntity;
import com.shop.server.infrastructure.security.oauth2.session.InfoUserTShirt;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import org.springframework.stereotype.Component;

import java.util.Calendar;

@Component
public class AuditEntityListener {

    private final InfoUserTShirt infoUserTShirt;

    public AuditEntityListener(InfoUserTShirt infoUserTShirt) {
        this.infoUserTShirt = infoUserTShirt;
    }

    @PrePersist
    private void onCreate(AuditEntity entity) {
        entity.setCreatedDate(getCurrentTime());
        entity.setLastModifiedDate(getCurrentTime());
        entity.setNguoiTao(infoUserTShirt.getId());
        entity.setNguoiSua(infoUserTShirt.getId());
    }

    @PreUpdate
    private void onUpdate(AuditEntity entity) {
        entity.setLastModifiedDate(getCurrentTime());
        entity.setNguoiSua(infoUserTShirt.getId());
    }

    private long getCurrentTime() {
        return Calendar.getInstance().getTimeInMillis();
    }

}