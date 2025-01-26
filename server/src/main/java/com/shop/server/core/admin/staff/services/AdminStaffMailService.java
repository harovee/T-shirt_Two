package com.shop.server.core.admin.staff.services;

import com.shop.server.entities.main.NhanVien;

public interface AdminStaffMailService {

    void sendMailCreateStaff(NhanVien staff);

    void sendMailUpdateStaff(NhanVien staff);

}
