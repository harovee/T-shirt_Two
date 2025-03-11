package com.shop.server.core.client.my_page.service.impl;

import com.shop.server.core.client.my_page.model.request.ClientBillRequest;
import com.shop.server.core.client.my_page.repository.ClientBillRepository;
import com.shop.server.core.client.my_page.service.ClientBillService;
import com.shop.server.core.common.base.PageableObject;
import com.shop.server.core.common.base.ResponseObject;
import com.shop.server.infrastructure.constants.module.Message;
import com.shop.server.utils.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientBillServiceImpl implements ClientBillService {

    private final ClientBillRepository clientBillRepository;

    @Override
    public ResponseObject<?> getAllBill(ClientBillRequest request) {
        Pageable pageable = Helper.createPageable(request);
        return new ResponseObject<>(
                PageableObject.of(clientBillRepository.getAllBillsByRequest(pageable, request)),
                HttpStatus.OK,
                Message.Success.GET_SUCCESS
        );
    }
}
