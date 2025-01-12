package com.shop.server.core.admin.hoa_tiet.controller;

import com.shop.server.core.admin.hoa_tiet.model.request.AdCreateUpdateHoaTietRequest;
import com.shop.server.core.admin.hoa_tiet.service.AdHoaTietService;
import com.shop.server.infrastructure.constants.module.MappingConstant;
import com.shop.server.utils.Helper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping(MappingConstant.API_ADMIN_PATTERN)
@RestController
@RequiredArgsConstructor
public class AdHoaTietController {

    private final AdHoaTietService adHoaTietService;

    @GetMapping("/get-list-pattern")
    public ResponseEntity<?> getListHoaTiet() {
        return Helper.createResponseEntity(adHoaTietService.getListHoaTiet());
    }


    @PostMapping()
    public ResponseEntity<?> createHoaTiet(@Valid @RequestBody final AdCreateUpdateHoaTietRequest request) {
        return Helper.createResponseEntity(adHoaTietService.createHoaTiet(request));
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
