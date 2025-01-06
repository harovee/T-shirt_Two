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

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "san_pham_giam_gia")
public class SanPhamGiamGia extends PrimaryEntity implements Serializable {

    @Column(name = "gia_sau_giam")
    private BigDecimal giaSauGiam;

    @ManyToOne
    @JoinColumn(name = "id_dot_giam_gia", referencedColumnName = "id")
    private DotGiamGia dotGiamGia;

    @ManyToOne
    @JoinColumn(name = "id_san_pham_chi_tiet", referencedColumnName = "id")
    private SanPhamChiTiet sanPhamChiTiet;

}
