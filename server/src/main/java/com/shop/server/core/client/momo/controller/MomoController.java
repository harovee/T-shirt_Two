package com.shop.server.core.client.momo.controller;

import com.shop.server.core.client.momo.model.MomoRequest;
import com.shop.server.core.client.momo.model.MomoResponse;
import com.shop.server.core.client.momo.services.MomoServices;
import com.shop.server.infrastructure.constants.module.MappingConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@CrossOrigin("*")
@RequestMapping(MappingConstant.API_CLIENT_PAYMENT)
public class MomoController {

    private final MomoServices momoServices;

    @PostMapping("/create")
    ResponseEntity<?> createMomo() {
        return new ResponseEntity<>(momoServices.createMomo(), HttpStatus.OK);
    }

//    @GetMapping("/momo-callback")

}
