package com.shop.server.core.admin.client.services.impl;

import com.shop.server.core.admin.client.models.requests.ClientFindProductRequest;
import com.shop.server.core.admin.client.models.requests.ClientProductRequest;
import com.shop.server.core.admin.client.repositories.AdminClientRepository;
import com.shop.server.core.admin.client.services.AdminClientService;
import com.shop.server.core.common.base.PageableObject;
import com.shop.server.core.common.base.ResponseObject;
import com.shop.server.entities.main.KhachHang;
import com.shop.server.entities.main.NhanVien;
import com.shop.server.infrastructure.constants.module.Message;
import com.shop.server.infrastructure.security.oauth2.session.InfoUserTShirt;
import com.shop.server.utils.DateTimeUtil;
import com.shop.server.utils.Helper;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class AdminClientServiceImpl implements AdminClientService {

    private final AdminClientRepository adminClientRepository;

    private final InfoUserTShirt infoUserTShirt;

    public AdminClientServiceImpl(AdminClientRepository adminClientRepository, InfoUserTShirt infoUserTShirt) {
        this.adminClientRepository = adminClientRepository;
        this.infoUserTShirt = infoUserTShirt;
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
        return new ResponseObject<>(
                adminClientRepository.getClientDetail(id),
                HttpStatus.OK,
                Message.Success.GET_SUCCESS
        );
    }

    @Override
    public ResponseObject<?> createClient(ClientProductRequest request) {
        if (adminClientRepository.existsClientByEmail(request.getEmail())) {
            return ResponseObject.errorForward(
                    HttpStatus.BAD_REQUEST,
                    Message.Response.DUPLICATE + ", email"
            );
        }
        KhachHang client = new KhachHang();
        client.setUsername(request.getUserName());
        client.setPassword(request.getPassword());
        client.setFullName(request.getName());
        client.setEmail(request.getEmail());
        Long count = adminClientRepository.count() + 1;
        String formattedCode = String.format("%05d", count);
        client.setCode(formattedCode);
        client.setBirthday(DateTimeUtil.convertStringToTimeStampSecond(request.getBirthday()));
        client.setGender(request.getGender());
        client.setPhoneNumber(request.getPhoneNumber());
        client.setDeleted(false);
        client.setNguoiTao(infoUserTShirt.getId());
        client.setNguoiSua(infoUserTShirt.getId());
        adminClientRepository.save(client);
        return ResponseObject.successForward(
                HttpStatus.CREATED,
                Message.Success.CREATE_SUCCESS
        );
    }

    @Override
    public ResponseObject<?> updateClient(String id, ClientProductRequest request) {
        Optional<KhachHang> clientOptional = adminClientRepository.findById(id);
        if (clientOptional.isEmpty()) {
            return ResponseObject.errorForward(
                    HttpStatus.BAD_REQUEST,
                    Message.Response.NOT_FOUND + ", khách hàng"
            );
        }
        try {
            KhachHang client = clientOptional.get();
            client.setUsername(request.getUserName());
            client.setPassword(request.getPassword());
            client.setFullName(request.getName());
            client.setEmail(request.getEmail());
            client.setBirthday(DateTimeUtil.convertStringToTimeStampSecond(request.getBirthday()));
            client.setGender(request.getGender());
            client.setPhoneNumber(request.getPhoneNumber());
            client.setNguoiSua(infoUserTShirt.getId());
            adminClientRepository.save(client);
        } catch (Exception e) {
            return ResponseObject.errorForward(
                    HttpStatus.BAD_REQUEST,
                    e.getMessage()
            );
        }
        return ResponseObject.successForward(
                HttpStatus.CREATED,
                Message.Success.UPDATE_SUCCESS
        );
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
