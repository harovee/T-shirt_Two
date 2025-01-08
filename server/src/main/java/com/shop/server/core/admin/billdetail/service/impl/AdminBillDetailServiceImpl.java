package com.shop.server.core.admin.billdetail.service.impl;

import com.shop.server.core.admin.billdetail.model.request.AdminFindBillDetailRequest;
import com.shop.server.core.admin.billdetail.repository.AdminBillDetailRepository;
import com.shop.server.core.admin.billdetail.service.AdminBillDetailService;
import com.shop.server.core.common.base.PageableObject;
import com.shop.server.core.common.base.ResponseObject;
import com.shop.server.infrastructure.constants.module.Message;
import com.shop.server.utils.Helper;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class AdminBillDetailServiceImpl implements AdminBillDetailService {
    private final AdminBillDetailRepository adminBillDetailRepository;

    public AdminBillDetailServiceImpl(AdminBillDetailRepository adminBillDetailRepository) {
        this.adminBillDetailRepository = adminBillDetailRepository;
    }

    @Override
    public ResponseObject<?> getBillDetailsByRequest(AdminFindBillDetailRequest request) {
        Pageable pageable = Helper.createPageable(request);
        return new ResponseObject<>(
                PageableObject.of(adminBillDetailRepository.getAdminBillDetailByRequest(pageable, request)),
                HttpStatus.OK,
                Message.Success.GET_SUCCESS
        );
    }

    @Override
    public ResponseObject<?> getBillDetailById(String id) {
        return new ResponseObject<>(
                adminBillDetailRepository.getAdminBillDetailById(id),
                HttpStatus.OK,
                Message.Success.GET_SUCCESS
        );
    }
}
