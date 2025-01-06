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
@Table(name = "kich_co")
public class KichCo extends PrimaryEntity implements Serializable {

    @Column(name = "ma_kich_co", unique = true)
    private String maKichCo;

    @Column(name = "ten")
    private String ten;

    @Column(name = "chieu_cao_min")
    private Float chieuCaoMin;

    @Column(name = "chieu_cao_max")
    private Float chieuCaoMax;

    @Column(name = "can_nang_min")
    private Float canNangMin;

    @Column(name = "can_nang_max")
    private Float canNangMax;

}
