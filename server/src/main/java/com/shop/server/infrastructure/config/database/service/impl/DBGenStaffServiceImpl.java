package com.shop.server.infrastructure.config.database.service.impl;

import com.shop.server.entities.main.NhanVien;
import com.shop.server.infrastructure.config.database.repository.DBGenStaffRepository;
import com.shop.server.infrastructure.config.database.service.DBGenStaffService;
import com.shop.server.infrastructure.constants.module.Role;
import com.shop.server.infrastructure.constants.module.Status;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DBGenStaffServiceImpl implements DBGenStaffService {

    private final DBGenStaffRepository staffRepository;

    List<NhanVien> staffs = List.of(
            new NhanVien(
                    "AdminCO1", "co1", "Hảo Hảo Hảo Ni", 1L, Boolean.TRUE,
                    "0849070561", "haove4798@gmail.com", "Abc1234@", "01242063242342",
                    null, null, Role.ADMIN, Status.ACTIVE
            ),
            new NhanVien(
                    "AdminCO2", "co2", "Ánh Quỳnh", 1L, Boolean.TRUE,
                    "0849070562", "trananhquynh2102@gmail.com", "Abc1234@", "01242352442342",
                    null, null, Role.ADMIN, Status.ACTIVE
            ),
            new NhanVien(
                    "AdminCO3", "co3", "Phạm Thị Lanh Anh", 1L, Boolean.TRUE,
                    "0849070563", "lananh1503.dev@gmail.com", "Abc1234@", "01242843242342",
                    null, null, Role.ADMIN, Status.ACTIVE
            ),
            new NhanVien(
                    "AdminCO4", "co4", "Bùi Minh Hiếu", 1L, Boolean.TRUE,
                    "0849070564", "hieubuiminh11091997@gmail.com", "Abc1234@", "0124234532342",
                    null, null, Role.ADMIN, Status.ACTIVE
            ),
            new NhanVien(
                    "AdminCO4.1", "co4.1", "Bùi Minh Hiếu", 1L, Boolean.TRUE,
                    "0849070565", "buiminhhieu11091997@gmail.com", "Abc1234@", "01248653242342",
                    null, null, Role.ADMIN, Status.ACTIVE
            ),
            new NhanVien(
                    "AdminCO5", "co5", "Thảo", 1L, Boolean.TRUE,
                    "0849070566", "thao@gmail.com", "Abc1234@", "01212353242342",
                    null, null, Role.ADMIN, Status.ACTIVE
            ),
            new NhanVien(
                    "AdminCO6", "co6", "Thảnh", 1L, Boolean.TRUE,
                    "0849070867", "hthanh292k@gmail.com", "Abc1234@", "01248353242342",
                    null, null, Role.ADMIN, Status.ACTIVE
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
        staffRepository.saveAll(staffs);
    }

}
