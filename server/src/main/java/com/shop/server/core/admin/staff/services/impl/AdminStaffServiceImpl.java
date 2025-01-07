package com.shop.server.core.admin.staff.services.impl;

import com.shop.server.core.admin.staff.models.requests.AdminFindStaffRequest;
import com.shop.server.core.admin.staff.models.requests.AdminStaffRequest;
import com.shop.server.core.admin.staff.repositories.AdminStaffRepository;
import com.shop.server.core.admin.staff.services.AdminStaffService;
import com.shop.server.core.common.base.PageableObject;
import com.shop.server.core.common.base.ResponseObject;
import com.shop.server.entities.main.NhanVien;
import com.shop.server.infrastructure.constants.module.Message;
import com.shop.server.utils.Helper;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class AdminStaffServiceImpl implements AdminStaffService {

    private final AdminStaffRepository adminStaffRepository;

    public AdminStaffServiceImpl(AdminStaffRepository adminStaffRepository) {
        this.adminStaffRepository = adminStaffRepository;
    }

    @Override
    public ResponseObject<?> getStaffs(AdminFindStaffRequest request) {
        Pageable pageable = Helper.createPageable(request);
        return new ResponseObject<>(
                PageableObject.of(adminStaffRepository.getStaffByRequest(pageable, request)),
                HttpStatus.OK,
                Message.Success.GET_SUCCESS
        );
    }

    @Override
    public ResponseObject<?> getStaffById(String id) {
        return null;
    }

    @Override
    public ResponseObject<?> createStaff(AdminStaffRequest request) {
        if (adminStaffRepository.existsStaffByEmail(request.getEmail())) {
            return ResponseObject.errorForward(
                    HttpStatus.BAD_REQUEST,
                    Message.Response.DUPLICATE + ", email"
            );
        }
        NhanVien nhanVien = new NhanVien();
        nhanVien.setUserName(request.getName());
        nhanVien.setEmail(request.getEmail());
        nhanVien.setDeleted(false);
        adminStaffRepository.save(nhanVien);
        return ResponseObject.successForward(
                HttpStatus.CREATED,
                Message.Success.CREATE_SUCCESS
        );
    }

    @Override
    public ResponseObject<?> updateStaff(String id, AdminStaffRequest request) {
        return null;
    }

    @Override
    public ResponseObject<?> changeStatusStaff(String id) {
        Optional<NhanVien> nhanVienOptional = adminStaffRepository.findById(id);
        if (nhanVienOptional.isEmpty()) {
            return ResponseObject.errorForward(
                    HttpStatus.BAD_REQUEST,
                    Message.Response.NOT_FOUND + ", nhân viên"
            );
        }
        NhanVien nhanVien = nhanVienOptional.get();
        nhanVien.setDeleted(!nhanVien.getDeleted());
        adminStaffRepository.save(nhanVien);
        return ResponseObject.successForward(
                HttpStatus.CREATED,
                Message.Success.UPDATE_SUCCESS
        );
    }
}
