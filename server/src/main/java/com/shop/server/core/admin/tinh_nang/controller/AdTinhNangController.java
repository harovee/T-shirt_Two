package com.shop.server.core.admin.tinh_nang.controller;

import com.shop.server.core.admin.tinh_nang.model.request.AdCreateUpdateTinhNangRequest;
import com.shop.server.core.admin.tinh_nang.service.AdTinhNangService;
import com.shop.server.infrastructure.constants.module.MappingConstant;
import com.shop.server.utils.Helper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping(MappingConstant.API_ADMIN_FEATURE)
@RestController
@RequiredArgsConstructor
public class AdTinhNangController {

    private final AdTinhNangService adTinhNangService;

    @GetMapping("/get-list-feature")
    public ResponseEntity<?> getListTinhNang() {
        return Helper.createResponseEntity(adTinhNangService.getListTinhNang());
    }


    @PostMapping()
    public ResponseEntity<?> createCoAo(@Valid @RequestBody final AdCreateUpdateTinhNangRequest request) {
        return Helper.createResponseEntity(adTinhNangService.createTinhNang(request));
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
