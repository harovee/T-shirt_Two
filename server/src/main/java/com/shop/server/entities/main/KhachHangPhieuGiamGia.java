package com.shop.server.entities.main;

import com.shop.server.entities.base.PrimaryEntity;
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
@Table(name = "khach_hang_phieu_giam_gia")
public class KhachHangPhieuGiamGia extends PrimaryEntity implements Serializable {

    @ManyToOne
    @JoinColumn(name = "id_khach_hang", referencedColumnName = "id")
    private KhachHang khachHang;

    @ManyToOne
    @JoinColumn(name = "id_phieu_giam_gia", referencedColumnName = "id")
    private PhieuGiamGia phieuGiamGia;

}
