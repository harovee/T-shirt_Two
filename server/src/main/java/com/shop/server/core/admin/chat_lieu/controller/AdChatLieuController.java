package com.shop.server.core.admin.chat_lieu.controller;

import com.shop.server.core.admin.chat_lieu.model.request.AdCreateUpdateChatLieuRequest;
import com.shop.server.core.admin.chat_lieu.model.request.AdFindChatLieuRequest;
import com.shop.server.core.admin.chat_lieu.service.AdChatLieuService;
import com.shop.server.core.admin.product.models.requests.AdminFindProductRequest;
import com.shop.server.core.admin.product.models.requests.AdminProductRequest;
import com.shop.server.core.admin.product.services.AdminProductService;
import com.shop.server.infrastructure.constants.module.MappingConstant;
import com.shop.server.utils.Helper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping(MappingConstant.API_ADMIN_MATERIAL)
@RestController
@RequiredArgsConstructor
public class AdChatLieuController {

    private final AdChatLieuService adChatLieuService;

    @GetMapping()
    public ResponseEntity<?> getAllChatLieus(@Valid final AdFindChatLieuRequest request) {
        return Helper.createResponseEntity(adChatLieuService.getAllChatLieus(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getChatLieuDetail(@PathVariable final String id) {
        return Helper.createResponseEntity(adChatLieuService.getChatLieu(id));
    }

    @PostMapping()
    public ResponseEntity<?> createChatLieu(@Valid @RequestBody final AdCreateUpdateChatLieuRequest request) {
        return Helper.createResponseEntity(adChatLieuService.createChatLieu(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateChatLieu(@PathVariable String id, @Valid @RequestBody final AdCreateUpdateChatLieuRequest request) {
        return Helper.createResponseEntity(adChatLieuService.updateChatLieu(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteChatLieu(@PathVariable String id) {
        return Helper.createResponseEntity(adChatLieuService.deleted(id));
    }

    @GetMapping("/get-list-material")
    public ResponseEntity<?> getListChatLieu() {
        return Helper.createResponseEntity(adChatLieuService.getListChatLieu());
    }

}
