package com.shop.server.core.client.momo.controller;

import com.shop.server.core.client.momo.model.request.ClientMomoRequest;
import com.shop.server.core.client.momo.services.IMomoService;
import com.shop.server.infrastructure.constants.module.MappingConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
@RequestMapping(MappingConstant.API_CLIENT_PAYMENT)
public class MomoController {

    private final IMomoService momoServices;

    @PostMapping("momo")
    ResponseEntity<?> createMomo(@RequestBody ClientMomoRequest request) {
        return new ResponseEntity<>(momoServices.createMomo(request), HttpStatus.OK);
    }

    @GetMapping("momo-callback")
    public String momoCallback(@RequestParam Map<String, String>  request) {
        Integer resultCode = Integer.valueOf(request.get("resultCode"));
        if (resultCode == 0) {
            return "Thanh toán thành công";
        }
        else {
            return "Thanh toán thất bại";
        }
    }
}
