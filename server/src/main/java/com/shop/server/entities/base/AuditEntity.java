package com.shop.server.entities.base;

import com.shop.server.infrastructure.config.database.AuditorAwareConfig;
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
@EntityListeners({AuditEntityListener.class, AuditorAwareConfig.class})
public abstract class AuditEntity {

    @Column(name = "ngay_tao", updatable = false)
    @CreatedDate
    private Long createdDate;

    @Column (name = "ngay_sua")
    @LastModifiedDate
    private Long lastModifiedDate;

    @Column(name = "nguoi_tao")
    @CreatedBy
    private String nguoiTao;

    @Column(name = "nguoi_sua")
    @LastModifiedBy
    private String nguoiSua;

    @Column(name = "deleted",precision = 0)
    private Boolean deleted;

}