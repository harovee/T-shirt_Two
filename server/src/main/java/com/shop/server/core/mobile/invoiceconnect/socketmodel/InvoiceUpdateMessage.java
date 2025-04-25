package com.shop.server.core.mobile.invoiceconnect.socketmodel;

import com.shop.server.core.mobile.invoiceconnect.businessmodel.InvoiceInfo;

public class InvoiceUpdateMessage extends WebSocketMessage {

    public InvoiceUpdateMessage(InvoiceInfo invoice) {
        setType("invoice_update");
        setData(invoice);
    }

}
