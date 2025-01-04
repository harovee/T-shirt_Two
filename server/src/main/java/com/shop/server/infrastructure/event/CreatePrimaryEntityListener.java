package com.shop.server.infrastructure.event;

import com.shop.server.entities.base.PrimaryEntity;
import jakarta.persistence.PrePersist;

import java.util.UUID;

public class CreatePrimaryEntityListener {

    @PrePersist
    private void onCreate(PrimaryEntity entity) {
        entity.setId(UUID.randomUUID().toString());
    }

}
