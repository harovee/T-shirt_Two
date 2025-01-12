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

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "khach_hang")
public class KhachHang extends PrimaryEntity implements Serializable {

    @Column(name = "username", unique = true)
    private String username;

    @Column(name = "ma_khach_hang", unique = true)
    private String code;

    @Column(name = "ho_va_ten", length = EntityProperties.LENGTH_NAME)
    private String fullName;

    @Column(name = "ngay_sinh")
    private Long birthday;

    @Column(name = "gioi_tinh")
    private Boolean gender;

    @Column(name = "so_dien_thoai")
    private String phoneNumber;

    @Column(nullable = false, length = EntityProperties.LENGTH_CODE, unique = true)
    private String email;

    @Column(name = "password", length = EntityProperties.LENGTH_PASSWORD)
    private String password;

    @Column(length = EntityProperties.LENGTH_CODE)
    private String subscriptionType;

    @Column(length = EntityProperties.LENGTH_URL)
    private String profilePicture;

}
