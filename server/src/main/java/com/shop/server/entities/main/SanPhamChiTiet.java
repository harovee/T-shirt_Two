package com.shop.server.entities.main;

import com.shop.server.entities.base.PrimaryEntity;
import com.shop.server.infrastructure.constants.module.Status;
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


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "san_pham_chi_tiet")
public class SanPhamChiTiet extends PrimaryEntity implements Serializable {

    @Column(name = "ma_san_pham_chi_tiet", unique = true)
    private String maSPCT;

    @Column(name = "ten")
    private String ten;

    @Column(name = "gioi_tinh")
    private Boolean gioiTinh;

    @Column(name = "gia")
    private BigDecimal gia;

    @Column(name = "so_luong")
    private Long soLuong;

    @ManyToOne
    @JoinColumn(name = "id_san_pham", referencedColumnName = "id")
    private SanPham sanPham;

    @ManyToOne
    @JoinColumn(name = "id_mau_sac", referencedColumnName = "id")
    private MauSac mauSac;

    @ManyToOne
    @JoinColumn(name = "id_kich_co", referencedColumnName = "id")
    private KichCo kichCo;

    @ManyToOne
    @JoinColumn(name = "id_tinh_nang", referencedColumnName = "id")
    private TinhNang tinhNang;

    @ManyToOne
    @JoinColumn(name = "id_kieu_dang", referencedColumnName = "id")
    private KieuDang kieuDang;

    @ManyToOne
    @JoinColumn(name = "id_hoa_tiet", referencedColumnName = "id")
    private HoaTiet hoaTiet;

    @ManyToOne
    @JoinColumn(name = "id_co_ao", referencedColumnName = "id")
    private CoAo coAo;

    @ManyToOne
    @JoinColumn(name = "id_thuong_hieu", referencedColumnName = "id")
    private ThuongHieu thuongHieu;

    @ManyToOne
    @JoinColumn(name = "id_chat_lieu", referencedColumnName = "id")
    private ChatLieu chatLieu;

    @ManyToOne
    @JoinColumn(name = "id_tay_ao", referencedColumnName = "id")
    private TayAo tayAo;

    @Column(name = "trang_thai")
    private Status trangThai;

}
