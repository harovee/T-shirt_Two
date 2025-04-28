package com.shop.websocket.model.entity;

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
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "message")
public class ChatMessage extends PrimaryEntity implements Serializable {

    @Column(name = "loai_tin_nhan")
    private MessageType type;

    @Column(name = "noi_dung")
    private String content;

    @Column(name = "da_xem")
    private Boolean isRead = false;

    @Column(name = "nguoi_gui")
    private String sender;

    @Column(name = "ma_phong")
    private String roomId;

    @Column(name = "nguoi_nhan")
    private String receiver;

    public enum MessageType {
        CHAT,
        JOIN,
        LEAVE
    }
}
