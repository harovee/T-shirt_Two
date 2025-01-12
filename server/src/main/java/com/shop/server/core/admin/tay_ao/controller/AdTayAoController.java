package com.shop.server.core.admin.tay_ao.controller;

import com.shop.server.core.admin.tay_ao.model.request.AdCreateUpdateTayAoRequest;
import com.shop.server.core.admin.tay_ao.service.AdTayAoService;
import com.shop.server.infrastructure.constants.module.MappingConstant;
import com.shop.server.utils.Helper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping(MappingConstant.API_ADMIN_SLEEVE)
@RestController
@RequiredArgsConstructor
public class AdTayAoController {

    private final AdTayAoService adTayAoService;

    @GetMapping("/get-list-sleeve")
    public ResponseEntity<?> getListTayAo() {
        return Helper.createResponseEntity(adTayAoService.getListTayAo());
    }


    @PostMapping()
    public ResponseEntity<?> createTayAo(@Valid @RequestBody final AdCreateUpdateTayAoRequest request) {
        return Helper.createResponseEntity(adTayAoService.createTayAo(request));
    }

//    @PutMapping("/{id}")
//    public ResponseEntity<?> updateChatLieu(@PathVariable String id, @Valid @RequestBody final AdCreateUpdateChatLieuRequest request) {
//        return Helper.createResponseEntity(adChatLieuService.updateChatLieu(id, request));
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<?> deleteChatLieu(@PathVariable String id) {
//        return Helper.createResponseEntity(adChatLieuService.deleted(id));
//    }

}
