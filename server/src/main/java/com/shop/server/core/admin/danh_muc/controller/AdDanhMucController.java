package com.shop.server.core.admin.danh_muc.controller;

import com.shop.server.core.admin.co_ao.model.request.AdCreateUpdateCoAoRequest;
import com.shop.server.core.admin.co_ao.service.AdCoAoService;
import com.shop.server.core.admin.danh_muc.model.request.AdCreateUpdateDanhMucRequest;
import com.shop.server.core.admin.danh_muc.service.AdDanhMucService;
import com.shop.server.infrastructure.constants.module.MappingConstant;
import com.shop.server.utils.Helper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping(MappingConstant.API_ADMIN_CATEGORY)
@RestController
@RequiredArgsConstructor
public class AdDanhMucController {

    private final AdDanhMucService adDanhMucService;

    @GetMapping("/get-list-category")
    public ResponseEntity<?> getListDanhMuc() {
        return Helper.createResponseEntity(adDanhMucService.getListDanhMuc());
    }


    @PostMapping()
    public ResponseEntity<?> createDanhMuc(@Valid @RequestBody final AdCreateUpdateDanhMucRequest request) {
        return Helper.createResponseEntity(adDanhMucService.createDanhMuc(request));
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
