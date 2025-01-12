package com.shop.server.core.admin.mau_sac.controller;

import com.shop.server.core.admin.mau_sac.model.request.AdCreateUpdateMauSacRequest;
import com.shop.server.core.admin.mau_sac.service.AdMauSacService;
import com.shop.server.infrastructure.constants.module.MappingConstant;
import com.shop.server.utils.Helper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping(MappingConstant.API_ADMIN_COLOR)
@RestController
@RequiredArgsConstructor
public class AdMauSacController {

    private final AdMauSacService adMauSacService;

    @GetMapping("/get-list-color")
    public ResponseEntity<?> getListMauSac() {
        return Helper.createResponseEntity(adMauSacService.getListMauSac());
    }


    @PostMapping()
    public ResponseEntity<?> createCoAo(@Valid @RequestBody final AdCreateUpdateMauSacRequest request) {
        return Helper.createResponseEntity(adMauSacService.createMauSac(request));
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
