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
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "hoa_don")
public class HoaDon extends PrimaryEntity implements Serializable {

    @Column(name = "ma_hoa_don", unique = true)
    private String ma;

    @Column(name = "tien_giam")
    private BigDecimal tienGiam;

    @Column(name = "tong_tien")
    private BigDecimal tongTien;

    @Column(name = "loai_hoa_don")
    private String loaiHD;

    @Column(name = "phuong_thuc_nhan")
    private String phuongThucNhan;

    @Column(name = "ten_nguoi_nhan")
    private String tenNguoiNhan;

    @Column(name = "so_dien_thoai")
    private String soDienThoai;

    @Column(name = "dia_chi_nguoi_nhan")
    private String diaChiNguoiNhan;

    @Column(name = "email_nguoi_nhan")
    private String emailNguoiNhan;

    @Column(name = "tinh")
    private String tinh;

    @Column(name = "huyen")
    private String huyen;

    @Column(name = "xa")
    private String xa;

    @Column(name = "tien_ship")
    private BigDecimal tienShip;

    @Column(name = "ngay_ship")
    private LocalDate ngayShip;

    @Column(name = "ghi_chu")
    private String ghiChu;

    @Column(name = "trang_thai")
    private String trangThai;

    @ManyToOne
    @JoinColumn(name = "id_nhan_vien", referencedColumnName = "id")
    private NhanVien nhanVien;

    @ManyToOne
    @JoinColumn(name = "id_phieu_giam_gia", referencedColumnName = "id")
    private PhieuGiamGia phieuGiamGia;

    @ManyToOne
    @JoinColumn(name = "id_khach_hang", referencedColumnName = "id")
    private KhachHang khachHang;

}
