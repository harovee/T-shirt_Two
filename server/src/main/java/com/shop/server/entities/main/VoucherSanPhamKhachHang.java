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

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "voucher_san_pham_khach_hang")
public class VoucherSanPhamKhachHang extends PrimaryEntity implements Serializable {
    @Column(name = "ma_voucher_san_pham_khach_hang",unique = true)
    private String ma;

    @Column(name = "id_san_pham")
    private String idSanPham;

    @Column(name = "id_khach_hang")
    private String idKhachHang;

    @Column(name = "id_phieu_giam_gia")
    private String idPhieuGiamGia;

    @Column(name = "so_luong_san_pham")
    private Integer soLuongSanPham;

}
