package com.shop.server.core.admin.payment_method.service.impl;

import com.shop.server.core.admin.payment_method.repository.AdminPayMentMethodRepository;
import com.shop.server.core.admin.payment_method.service.AdminPaymentMethodService;
import com.shop.server.core.common.base.ResponseObject;
import com.shop.server.infrastructure.constants.module.Message;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class AdminPaymentMethodServiceImpl implements AdminPaymentMethodService {
    public final AdminPayMentMethodRepository adminPayMentMethodRepository;

    public AdminPaymentMethodServiceImpl(AdminPayMentMethodRepository adminPayMentMethodRepository) {
        this.adminPayMentMethodRepository = adminPayMentMethodRepository;
    }


    @Override
    public ResponseObject<?> getAdminPaymentMethod() {
        return new ResponseObject<>(
                adminPayMentMethodRepository.getAllPaymentMethods(),
                HttpStatus.OK,
                Message.Success.GET_SUCCESS
        );
    }
}
