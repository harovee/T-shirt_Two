package com.shop.server.core.admin.bill.service.impl;

import com.shop.server.core.admin.bill.model.request.AdminFindBillRequest;
import com.shop.server.core.admin.bill.repository.AdminBillRepository;
import com.shop.server.core.admin.bill.service.AdminBillService;
import com.shop.server.core.common.base.PageableObject;
import com.shop.server.core.common.base.ResponseObject;
import com.shop.server.infrastructure.constants.module.Message;
import com.shop.server.utils.Helper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class AdminBillServiceImpl implements AdminBillService {
    private final AdminBillRepository adminBillRepository;

    public AdminBillServiceImpl(AdminBillRepository adminBillRepository) {
        this.adminBillRepository = adminBillRepository;
    }

    @Override
    public ResponseObject<?> getBills(AdminFindBillRequest request) {
        Pageable pageable = Helper.createPageable(request);
        return new ResponseObject<>(
                PageableObject.of(adminBillRepository.getBillsByRequest(pageable, request)),
                HttpStatus.OK,
                Message.Success.GET_SUCCESS
        );
    }

    @Override
    public ResponseObject<?> getDetailBillById(String id) {
        return new ResponseObject<>(
                adminBillRepository.getDetailBillById(id),
                HttpStatus.OK,
                Message.Success.GET_SUCCESS
        );
    }
}
