package com.shop.server.core.admin.bill.service;

import com.shop.server.core.admin.bill.model.request.AdminSendEmailRequest;
import com.shop.server.entities.main.HoaDon;
import com.shop.server.entities.main.PhieuGiamGia;

public interface AdminBillSendMailService {
    void sendMailCreateBill(AdminSendEmailRequest request);

    void sendMailUpdateBill(AdminSendEmailRequest request);

    void sendMailCancelBill(AdminSendEmailRequest request);
}
