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
@Table(name = "hoa_tiet")
public class HoaTiet extends PrimaryEntity implements Serializable {

    @Column(name = "ma_hoa_tiet")
    private String maHoaTiet;

    @Column(name = "ten")
    private String ten;

}
