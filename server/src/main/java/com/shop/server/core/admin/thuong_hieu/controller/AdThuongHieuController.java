package com.shop.server.core.admin.thuong_hieu.controller;

import com.shop.server.core.admin.thuong_hieu.model.request.AdCreateUpdateThuongHieuRequest;
import com.shop.server.core.admin.thuong_hieu.service.AdThuongHieuService;
import com.shop.server.infrastructure.constants.module.MappingConstant;
import com.shop.server.utils.Helper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping(MappingConstant.API_ADMIN_TRADEMARK)
@RestController
@RequiredArgsConstructor
public class AdThuongHieuController {

    private final AdThuongHieuService adThuongHieuService;

    @GetMapping("/get-list-trademark")
    public ResponseEntity<?> getListThuongHieu() {
        return Helper.createResponseEntity(adThuongHieuService.getListThuongHieu());
    }


    @PostMapping()
    public ResponseEntity<?> createThuongHieu(@Valid @RequestBody final AdCreateUpdateThuongHieuRequest request) {
        return Helper.createResponseEntity(adThuongHieuService.createThuongHieu(request));
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
