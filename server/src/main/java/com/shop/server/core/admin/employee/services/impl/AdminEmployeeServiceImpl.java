package com.shop.server.core.admin.employee.services.impl;

import com.shop.server.core.admin.employee.models.requests.EmployeeFindProductRequest;
import com.shop.server.core.admin.employee.models.requests.EmployeeProductRequest;
import com.shop.server.core.admin.employee.repositories.AdminEmployeeRepository;
import com.shop.server.core.admin.employee.services.AdminEmployeeService;
import com.shop.server.core.common.base.PageableObject;
import com.shop.server.core.common.base.ResponseObject;
import com.shop.server.entities.main.KhachHang;
import com.shop.server.infrastructure.constants.module.Message;
import com.shop.server.utils.Helper;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class AdminEmployeeServiceImpl implements AdminEmployeeService {

    private final AdminEmployeeRepository adminEmployeeRepository;

    public AdminEmployeeServiceImpl(AdminEmployeeRepository adminEmployeeRepository) {
        this.adminEmployeeRepository = adminEmployeeRepository;
    }

    @Override
    public ResponseObject<?> getEmployees(EmployeeFindProductRequest request) {
        Pageable pageable = Helper.createPageable(request);
        return new ResponseObject<>(
                PageableObject.of(adminEmployeeRepository.getEmployeeByRequest(pageable, request)),
                HttpStatus.OK,
                Message.Success.GET_SUCCESS
        );
    }

    @Override
    public ResponseObject<?> getEmployeeById(String id) {
        return null;
    }

    @Override
    public ResponseObject<?> createEmployee(EmployeeProductRequest request) {
        if (adminEmployeeRepository.existsKhachHangByEmail(request.getEmail())) {
            return ResponseObject.errorForward(
                    HttpStatus.BAD_REQUEST,
                    Message.Response.DUPLICATE + ", email"
            );
        }
        KhachHang khachHang = new KhachHang();
        khachHang.setHoVaTen(request.getName());
        khachHang.setEmail(request.getEmail());
        khachHang.setDeleted(false);
        adminEmployeeRepository.save(khachHang);
        return ResponseObject.successForward(
                HttpStatus.CREATED,
                Message.Success.CREATE_SUCCESS
        );
    }

    @Override
    public ResponseObject<?> updateEmployee(String id, EmployeeProductRequest request) {
        return null;
    }

    @Override
    public ResponseObject<?> changeStatusEmployee(String id) {
        Optional<KhachHang> khachHangOptional = adminEmployeeRepository.findById(id);
        if (khachHangOptional.isEmpty()) {
            return ResponseObject.errorForward(
                    HttpStatus.BAD_REQUEST,
                    Message.Response.NOT_FOUND + ", khách hàng"
            );
        }
        KhachHang khachHang = khachHangOptional.get();
        khachHang.setDeleted(!khachHang.getDeleted());
        adminEmployeeRepository.save(khachHang);
        return ResponseObject.successForward(
                HttpStatus.CREATED,
                Message.Success.UPDATE_SUCCESS
        );
    }
}
