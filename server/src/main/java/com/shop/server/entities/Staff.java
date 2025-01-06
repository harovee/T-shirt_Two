package com.shop.server.entities;

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
import java.time.LocalDate;

@Entity
@Table(name = "nhan_vien")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Staff extends PrimaryEntity implements Serializable {

    @Column(name = "ma_nhan_vien", unique = true)
    private String maNhanVien;

    @Column(name = "ho_va_ten", nullable = false, length = EntityProperties.LENGTH_NAME)
    private String userName;

    @Column(name = "ngay_sinh")
    private LocalDate ngaySinh;

    @Column(name = "gioi_tinh")
    private boolean gioiTinh;

    @Column(name = "so_dien_thoai")
    private String soDienThoai;

    @Column(nullable = false, length = EntityProperties.LENGTH_CODE)
    private String email;

    @Column(name = "password_hash", length = EntityProperties.LENGTH_PASSWORD)
    private String password;

    @Column(name = "password_secret", length = EntityProperties.LENGTH_PASSWORD_SECRET)
    private String passwordSecret;

    @Column(length = EntityProperties.LENGTH_CODE)
    private String subscriptionType;

    @Column(length = EntityProperties.LENGTH_URL)
    private String profilePicture;

    @Enumerated(EnumType.ORDINAL)
    private Role role;

    @Enumerated(EnumType.ORDINAL)
    private Status status;

}
