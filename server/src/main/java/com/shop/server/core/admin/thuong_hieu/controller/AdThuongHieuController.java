package com.shop.server.core.admin.thuong_hieu.controller;

import com.shop.server.core.admin.thuong_hieu.model.request.AdCreateUpdateThuongHieuRequest;
import com.shop.server.core.admin.thuong_hieu.model.request.AdFindThuongHieuRequest;
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

    @GetMapping()
    public ResponseEntity<?> getAllThuongHieus(@Valid final AdFindThuongHieuRequest request) {
        return Helper.createResponseEntity(adThuongHieuService.getAllThuongHieus(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getThuongHieuDetail(@PathVariable final String id) {
        return Helper.createResponseEntity(adThuongHieuService.getThuongHieu(id));
    }

    @PostMapping()
    public ResponseEntity<?> createThuongHieu(@Valid @RequestBody final AdCreateUpdateThuongHieuRequest request) {
        return Helper.createResponseEntity(adThuongHieuService.createThuongHieu(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateThuongHieu(@PathVariable String id, @Valid @RequestBody final AdCreateUpdateThuongHieuRequest request) {
        return Helper.createResponseEntity(adThuongHieuService.updateThuongHieu(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteThuongHieu(@PathVariable String id) {
        return Helper.createResponseEntity(adThuongHieuService.deleted(id));
    }

    @GetMapping("/get-list-trademark")
    public ResponseEntity<?> getListThuongHieu() {
        return Helper.createResponseEntity(adThuongHieuService.getListThuongHieu());
    }

}
