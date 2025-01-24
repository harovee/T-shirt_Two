package com.shop.server.core.admin.client.services;

import com.shop.server.entities.main.KhachHang;

public interface AdminClientMailService {

    void sendMailCreateClient(KhachHang client);

}
