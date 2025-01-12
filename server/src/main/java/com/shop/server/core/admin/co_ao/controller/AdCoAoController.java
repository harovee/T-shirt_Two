package com.shop.server.core.admin.co_ao.controller;

import com.shop.server.core.admin.chat_lieu.model.request.AdCreateUpdateChatLieuRequest;
import com.shop.server.core.admin.chat_lieu.model.request.AdFindChatLieuRequest;
import com.shop.server.core.admin.chat_lieu.service.AdChatLieuService;
import com.shop.server.core.admin.co_ao.model.request.AdCreateUpdateCoAoRequest;
import com.shop.server.core.admin.co_ao.service.AdCoAoService;
import com.shop.server.infrastructure.constants.module.MappingConstant;
import com.shop.server.utils.Helper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping(MappingConstant.API_ADMIN_COLLAR)
@RestController
@RequiredArgsConstructor
public class AdCoAoController {

    private final AdCoAoService adCoAoService;

    @GetMapping("/get-list-collar")
    public ResponseEntity<?> getListCoAo() {
        return Helper.createResponseEntity(adCoAoService.getListCoAo());
    }


    @PostMapping()
    public ResponseEntity<?> createCoAo(@Valid @RequestBody final AdCreateUpdateCoAoRequest request) {
        return Helper.createResponseEntity(adCoAoService.createCoAo(request));
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
