package com.shop.server.entities.main;

import com.shop.server.entities.base.PrimaryEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "dia_chi_nhan_vien")
public class StaffAddress extends PrimaryEntity implements Serializable {

    @Column(name = "ten")
    private String name;

    @Column(name = "so_dien_thoai")
    private String phoneNumber;

    @Column(name = "tinh_thanh_pho")
    private Long province;

    @Column(name = "quan_huyen")
    private Long district;

    @Column(name = "phuong_xa")
    private String ward;

    @Column(name = "so_nha")
    private String line;

    @Column(name = "mac_dinh")
    private Boolean isDefault;

    @Column(name = "mo_ta")
    private String description;

    @ManyToOne
    @JoinColumn(name = "id_nhan_vien", referencedColumnName = "id")
    private NhanVien StaffId;

}
