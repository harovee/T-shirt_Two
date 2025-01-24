package com.shop.server.entities.base;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@MappedSuperclass
public abstract class SimplePrimaryEntity implements IsIdentified {

    @Id
    @Column(length = 36, updatable = false)
    private String id;

}
