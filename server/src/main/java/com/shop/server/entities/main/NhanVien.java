package com.shop.server.entities.main;

import com.shop.server.entities.base.PrimaryEntity;
import com.shop.server.infrastructure.constants.module.EntityProperties;
import com.shop.server.infrastructure.constants.module.Role;
import com.shop.server.infrastructure.constants.module.Status;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Table(name = "nhan_vien")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NhanVien extends PrimaryEntity implements Serializable {

    @Column(name = "ma_nhan_vien", unique = true)
    private String code;

    @Column(name = "subCode")
    private String subCode;

    @Column(name = "ho_va_ten", length = EntityProperties.LENGTH_NAME)
    private String fullName;

    @Column(name = "ngay_sinh")
    private Long birthday;

    @Column(name = "gioi_tinh")
    private Boolean gender;

    @Column(name = "so_dien_thoai", unique = true)
    private String phoneNumber;

    @Column(length = EntityProperties.LENGTH_CODE)
    private String email;

    @Column(name = "password", length = EntityProperties.LENGTH_PASSWORD)
    private String password;

    @Column(name = "identity", length = EntityProperties.LENGTH_PASSWORD, unique = true)
    private String identity;

    @Column(length = EntityProperties.LENGTH_CODE)
    private String subscriptionType;

    @Column(length = EntityProperties.LENGTH_URL)
    private String profilePicture;

    @Enumerated(EnumType.ORDINAL)
    private Role role;

    @Enumerated(EnumType.ORDINAL)
    private Status status;

}
