package com.shop.server.entities.main;

import com.shop.server.entities.base.PrimaryEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "dot_giam_gia")
public class DotGiamGia extends PrimaryEntity implements Serializable {

    @Column(name = "ma_dot_giam_gia", unique = true)
    private String maDotGiamGia;

    @Column(name = "ten")
    private String ten;

    @Column(name = "loai")
    private String loai;

    @Column(name = "gia_tri")
    private String giaTri;

    @Column(name = "ngay_bat_dau")
    private LocalDate ngayBatDau;

    @Column(name = "ngay_ket_thuc")
    private LocalDate ngayKetThuc;

    @Column(name = "trang_thai")
    private String trangThai;

}
