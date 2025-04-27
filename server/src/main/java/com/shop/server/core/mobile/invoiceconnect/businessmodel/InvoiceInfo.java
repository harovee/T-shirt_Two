package com.shop.server.core.mobile.invoiceconnect.businessmodel;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class InvoiceInfo {

    private String deviceId;

    private String platForm;

    private Boolean connectConfirmed;

    private Long connectConfirmedTime;

    private String invoiceId;

    private String staffId;

    private String invoiceStatus;

    private Boolean paymentConfirmed;

    private Long paymentConfirmedTime;


}
