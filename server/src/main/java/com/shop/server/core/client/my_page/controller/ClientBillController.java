package com.shop.server.core.client.my_page.controller;

import com.shop.server.core.client.my_page.model.request.ClientBillRequest;
import com.shop.server.core.client.my_page.service.ClientBillService;
import com.shop.server.infrastructure.constants.module.MappingConstant;
import com.shop.server.utils.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(MappingConstant.API_CLIENT_MY_ORDER)
@RequiredArgsConstructor
public class ClientBillController {

    private final ClientBillService clientBillService;

    @GetMapping()
    public ResponseEntity<?> getAllClientBill(final ClientBillRequest request) {
        return Helper.createResponseEntity(clientBillService.getAllBill(request));
    }
}
