package com.shop.server.core.admin.client.services.impl;

import com.shop.server.core.admin.client.models.requests.ClientFindProductRequest;
import com.shop.server.core.admin.client.models.requests.ClientProductRequest;
import com.shop.server.core.admin.client.repositories.AdminClientRepository;
import com.shop.server.core.admin.client.services.AdminClientService;
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
public class AdminClientServiceImpl implements AdminClientService {

    private final AdminClientRepository adminClientRepository;

    public AdminClientServiceImpl(AdminClientRepository adminClientRepository) {
        this.adminClientRepository = adminClientRepository;
    }

    @Override
    public ResponseObject<?> getClients(ClientFindProductRequest request) {
        Pageable pageable = Helper.createPageable(request);
        return new ResponseObject<>(
                PageableObject.of(adminClientRepository.getClientByRequest(pageable, request)),
                HttpStatus.OK,
                Message.Success.GET_SUCCESS
        );
    }

    @Override
    public ResponseObject<?> getClientById(String id) {
        return null;
    }

    @Override
    public ResponseObject<?> createClient(ClientProductRequest request) {
        if (adminClientRepository.existsClientByEmail(request.getEmail())) {
            return ResponseObject.errorForward(
                    HttpStatus.BAD_REQUEST,
                    Message.Response.DUPLICATE + ", email"
            );
        }
        KhachHang khachHang = new KhachHang();
        khachHang.setHoVaTen(request.getName());
        khachHang.setEmail(request.getEmail());
        khachHang.setDeleted(false);
        adminClientRepository.save(khachHang);
        return ResponseObject.successForward(
                HttpStatus.CREATED,
                Message.Success.CREATE_SUCCESS
        );
    }

    @Override
    public ResponseObject<?> updateClient(String id, ClientProductRequest request) {
        return null;
    }

    @Override
    public ResponseObject<?> changeStatusClient(String id) {
        Optional<KhachHang> khachHangOptional = adminClientRepository.findById(id);
        if (khachHangOptional.isEmpty()) {
            return ResponseObject.errorForward(
                    HttpStatus.BAD_REQUEST,
                    Message.Response.NOT_FOUND + ", khách hàng"
            );
        }
        KhachHang khachHang = khachHangOptional.get();
        khachHang.setDeleted(!khachHang.getDeleted());
        adminClientRepository.save(khachHang);
        return ResponseObject.successForward(
                HttpStatus.CREATED,
                Message.Success.UPDATE_SUCCESS
        );
    }
}
