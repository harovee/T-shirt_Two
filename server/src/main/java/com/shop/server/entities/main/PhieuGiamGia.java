package com.shop.server.entities.main;

import com.shop.server.entities.base.PrimaryEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "phieu_giam_gia")
public class PhieuGiamGia extends PrimaryEntity implements Serializable {

    @Column(name = "ma_phieu_giam_gia")
    private String ma;

    @Column(name = "ten")
    private String ten;

    @Column(name = "so_luong")
    private Short soLuong;

    @Column(name = "dieu_kien_giam")
    private String dieuKienGiam;

    @Column(name = "gia_tri_giam")
    private Double giaTriGiam;

    @Column(name = "giam_toi_da")
    private String giamToiDa;

    @Column(name = "loai_giam")
    private Boolean loaiGiam; // 1 tiền // 0 %

    @Column(name = "loai_phieu")
    private Boolean kieu; // 1 cá nhân // 0 công khai

    @Column(name = "ngay_bat_dau")
    private LocalDate ngayBatDau;

    @Column(name = "ngay_ket_thuc")
    private LocalDate ngayKetThuc;

    @Column(name = "trang_thai")
    private String trangThai;
}
