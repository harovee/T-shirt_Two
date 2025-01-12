package com.shop.server.core.admin.kich_thuoc.controller;

import com.shop.server.core.admin.kich_thuoc.model.request.AdCreateUpdateKichCoRequest;
import com.shop.server.core.admin.kich_thuoc.service.AdKichCoService;
import com.shop.server.infrastructure.constants.module.MappingConstant;
import com.shop.server.utils.Helper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping(MappingConstant.API_ADMIN_SIZE)
@RestController
@RequiredArgsConstructor
public class AdKichCoController {

    private final AdKichCoService adKichCoService;

    @GetMapping("/get-list-size")
    public ResponseEntity<?> getListKichCo() {
        return Helper.createResponseEntity(adKichCoService.getListKichCo());
    }


    @PostMapping()
    public ResponseEntity<?> createKichCo(@Valid @RequestBody final AdCreateUpdateKichCoRequest request) {
        return Helper.createResponseEntity(adKichCoService.createKichCo(request));
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
