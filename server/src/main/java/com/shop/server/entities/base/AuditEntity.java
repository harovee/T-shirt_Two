package com.shop.server.entities.base;

import com.shop.server.infrastructure.event.AuditEntityListener;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDate;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditEntityListener.class)
public abstract class AuditEntity {

    @Column(name = "ngay_tao", updatable = false)
    private Long createdDate;

    @Column (name = "ngay_sua")
    private Long lastModifiedDate;

    @Column(name = "nguoi_tao")
    @CreatedBy
    private String nguoiTao;

    @Column(name = "nguoi_sua")
    @LastModifiedBy
    private String nguoiSua;

    @Column(name = "deleted")
    private Boolean deleted;

}