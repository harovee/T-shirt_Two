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
import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "chi_tiet_phuong_thuc_thanh_toan")
public class ChiTietPhuongThucThanhToan extends PrimaryEntity implements Serializable {

    @Column(name = "tien_khach_dua")
    private BigDecimal tienKhachDua;

    @Column(name = "so_tien_du")
    private BigDecimal soTienDu;

    @Column(name = "ghi_chu")
    private String ghiChu;

}
