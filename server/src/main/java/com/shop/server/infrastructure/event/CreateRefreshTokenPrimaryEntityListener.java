package com.shop.server.infrastructure.event;

import com.shop.server.entities.base.RefreshTokenPrimaryKey;
import jakarta.persistence.PrePersist;

import java.util.UUID;

public class CreateRefreshTokenPrimaryEntityListener {

    @PrePersist
    private void onCreate(RefreshTokenPrimaryKey entity) {
        entity.setId(UUID.randomUUID().toString());
    }

}
