package com.shop.server.core.mobile.invoiceconnect.socketmodel;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class WebSocketMessage {

    private String type;
    private Object data;

}
