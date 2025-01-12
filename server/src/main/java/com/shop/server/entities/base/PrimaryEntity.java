package com.shop.server.entities.base;

import com.shop.server.infrastructure.event.CreatePrimaryEntityListener;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(CreatePrimaryEntityListener.class)
public abstract class PrimaryEntity extends AuditEntity implements IsIdentified {

    @Id
    @Column(length = 36, updatable = false)
    private String id;

}