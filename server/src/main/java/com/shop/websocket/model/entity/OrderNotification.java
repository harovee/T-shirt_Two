package com.shop.websocket.model.entity;

import com.shop.server.entities.base.PrimaryEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "notification")
public class OrderNotification extends PrimaryEntity implements Serializable {
    @Column(name = "ma_hoa_don")
    @NotNull
    private String orderId;

    @Column(name = "noi_dung")
    @NotNull
    private String content;

    @Column(name = "da_xem")
    private Boolean isRead = false;

    @Column(name = "loai_thong_bao")
    private String notificationType;

    @Column(name = "da_gui")
    private Boolean isSent = false;
}
