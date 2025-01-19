package com.shop.server.core.admin.billhistory.service.impl;

import com.shop.server.core.admin.billhistory.model.request.AdminFindBillHistoryRequest;
import com.shop.server.core.admin.billhistory.repository.AdminBillHistoryRepository;
import com.shop.server.core.admin.billhistory.service.AdminBillHistoryService;
import com.shop.server.core.common.base.PageableObject;
import com.shop.server.core.common.base.ResponseObject;
import com.shop.server.infrastructure.constants.module.Message;
import com.shop.server.utils.Helper;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class AdminBillHistoryServiceImpl implements AdminBillHistoryService {
    private final AdminBillHistoryRepository adminBillHistoryRepository;

    public AdminBillHistoryServiceImpl(AdminBillHistoryRepository adminBillHistoryRepository) {
        this.adminBillHistoryRepository = adminBillHistoryRepository;
    }

    @Override
    public ResponseObject<?> getAdminBillHistory(AdminFindBillHistoryRequest request) {
        Pageable pageable = Helper.createPageable(request);
        return new ResponseObject<>(
                PageableObject.of(adminBillHistoryRepository.getAdminBillHistoryByRequest(pageable, request)),
                HttpStatus.OK,
                Message.Success.GET_SUCCESS
        );
    }


    @Override
    public ResponseObject<?> getAdminBillHistoryById(String id) {
        return new ResponseObject<>(
                adminBillHistoryRepository.getAdminBillHistoryById(id),
                HttpStatus.OK,
                Message.Success.GET_SUCCESS
        );
    }
}
