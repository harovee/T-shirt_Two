package com.shop.server.core.admin.danh_muc.controller;

import com.shop.server.core.admin.danh_muc.model.request.AdCreateUpdateDanhMucRequest;
import com.shop.server.core.admin.danh_muc.model.request.AdFindDanhMucRequest;
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

    @GetMapping()
    public ResponseEntity<?> getAllDanhMucs(@Valid final AdFindDanhMucRequest request) {
        return Helper.createResponseEntity(adDanhMucService.getAllDanhMucs(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDanhMucDetail(@PathVariable final String id) {
        return Helper.createResponseEntity(adDanhMucService.getDanhMuc(id));
    }

    @PostMapping()
    public ResponseEntity<?> createDanhMuc(@Valid @RequestBody final AdCreateUpdateDanhMucRequest request) {
        return Helper.createResponseEntity(adDanhMucService.createDanhMuc(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateDanhMuc(@PathVariable String id, @Valid @RequestBody final AdCreateUpdateDanhMucRequest request) {
        return Helper.createResponseEntity(adDanhMucService.updateDanhMuc(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDanhMuc(@PathVariable String id) {
        return Helper.createResponseEntity(adDanhMucService.deleted(id));
    }

    @GetMapping("/get-list-category")
    public ResponseEntity<?> getListDanhMuc() {
        return Helper.createResponseEntity(adDanhMucService.getListDanhMuc());
    }

}
