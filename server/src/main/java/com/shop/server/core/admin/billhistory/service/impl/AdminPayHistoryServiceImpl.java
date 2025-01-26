package com.shop.server.core.admin.billhistory.service.impl;

import com.shop.server.core.admin.billhistory.model.request.AdminFindPayHistoryRequest;
import com.shop.server.core.admin.billhistory.repository.AdminPayHistoryRepository;
import com.shop.server.core.admin.billhistory.service.AdminPayHistoryService;
import com.shop.server.core.common.base.ResponseObject;
import com.shop.server.infrastructure.constants.module.Message;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class AdminPayHistoryServiceImpl implements AdminPayHistoryService {
    private final AdminPayHistoryRepository adminPayHistoryRepository;

    public AdminPayHistoryServiceImpl(AdminPayHistoryRepository adminPayHistoryRepository) {
        this.adminPayHistoryRepository = adminPayHistoryRepository;
    }

    @Override
    public ResponseObject<?> getPayHistory(AdminFindPayHistoryRequest request) {
        return new ResponseObject<>(
                adminPayHistoryRepository.getPayHistory(request),
                HttpStatus.OK,
                Message.Success.GET_SUCCESS
        );
    }
}
