package com.shop.server.infrastructure.config.database.service.impl;

import com.shop.server.entities.main.NhanVien;
import com.shop.server.infrastructure.config.database.repository.DBGenStaffRepository;
import com.shop.server.infrastructure.config.database.service.DBGenStaffService;
import com.shop.server.infrastructure.constants.module.Role;
import com.shop.server.infrastructure.constants.module.Status;
import com.shop.server.utils.DefaultImageUtil;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DBGenStaffServiceImpl implements DBGenStaffService {

    private final DBGenStaffRepository staffRepository;

    List<NhanVien> staffs = List.of(
            new NhanVien(
                    "-CO1", "Admin", "Hảo Hảo Hảo Ni", 1L, Boolean.TRUE,
                    "0849070561", "haove4798@gmail.com", "Abc1234@", "01242063242342",
                    null, DefaultImageUtil.IMAGE, Role.ADMIN, Status.ACTIVE
            ),
            new NhanVien(
                    "-CO2", "Admin", "Ánh Quỳnh", 1L, Boolean.TRUE,
                    "0849070562", "trananhquynh2102@gmail.com", "Abc1234@", "01242352442342",
                    null, DefaultImageUtil.IMAGE, Role.ADMIN, Status.ACTIVE
            ),
            new NhanVien(
                    "-CO3", "Admin", "Phạm Thị Lanh Anh", 1L, Boolean.TRUE,
                    "0849070563", "lananh1503.dev@gmail.com", "Abc1234@", "01242843242342",
                    null, DefaultImageUtil.IMAGE, Role.ADMIN, Status.ACTIVE
            ),
            new NhanVien(
                    "-CO4", "Admin", "Bùi Minh Hiếu", 1L, Boolean.TRUE,
                    "0849070564", "hieubuiminh11091997@gmail.com", "Abc1234@", "0124234532342",
                    null, DefaultImageUtil.IMAGE, Role.ADMIN, Status.ACTIVE
            ),
            new NhanVien(
                    "-CO4.1", "Admin", "Bùi Minh Hiếu", 1L, Boolean.TRUE,
                    "0849070565", "buiminhhieu11091997@gmail.com", "Abc1234@", "01248653242342",
                    null, DefaultImageUtil.IMAGE, Role.ADMIN, Status.ACTIVE
            ),
            new NhanVien(
                    "-CO5", "Admin", "Thảo", 1L, Boolean.TRUE,
                    "0849070566", "thao@gmail.com", "Abc1234@", "01212353242342",
                    null, DefaultImageUtil.IMAGE, Role.ADMIN, Status.ACTIVE
            ),
            new NhanVien(
                    "-CO6", "Admin", "Thảnh", 1L, Boolean.TRUE,
                    "0849070867", "hthanh292k@gmail.com", "Abc1234@", "01248353242342",
                    null, DefaultImageUtil.IMAGE, Role.ADMIN, Status.ACTIVE
            )
    );

    public DBGenStaffServiceImpl(DBGenStaffRepository staffRepository) {
        this.staffRepository = staffRepository;
    }

    @Override
    public void generatorStaff() {
        if (staffRepository.count() > 0) {
            return;
        }
        for (NhanVien staff : staffs) {
            staff.setDeleted(false);
        }
        staffRepository.saveAll(staffs);
    }

}
