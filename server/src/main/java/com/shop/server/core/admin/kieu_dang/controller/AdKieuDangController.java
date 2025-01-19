package com.shop.server.core.admin.kieu_dang.controller;

import com.shop.server.core.admin.kieu_dang.model.request.AdCreateUpdateKieuDangRequest;
import com.shop.server.core.admin.kieu_dang.model.request.AdFindKieuDangRequest;
import com.shop.server.core.admin.kieu_dang.service.AdKieuDangService;
import com.shop.server.infrastructure.constants.module.MappingConstant;
import com.shop.server.utils.Helper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping(MappingConstant.API_ADMIN_STYLE)
@RestController
@RequiredArgsConstructor
public class AdKieuDangController {

    private final AdKieuDangService adKieuDangService;

    @GetMapping()
    public ResponseEntity<?> getAllKieuDangs(@Valid final AdFindKieuDangRequest request) {
        return Helper.createResponseEntity(adKieuDangService.getAllKieuDangs(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getKieuDangDetail(@PathVariable final String id) {
        return Helper.createResponseEntity(adKieuDangService.getKieuDang(id));
    }

    @PostMapping()
    public ResponseEntity<?> createKieuDang(@Valid @RequestBody final AdCreateUpdateKieuDangRequest request) {
        return Helper.createResponseEntity(adKieuDangService.createKieuDang(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateKieuDang(@PathVariable String id, @Valid @RequestBody final AdCreateUpdateKieuDangRequest request) {
        return Helper.createResponseEntity(adKieuDangService.updateKieuDang(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteKieuDang(@PathVariable String id) {
        return Helper.createResponseEntity(adKieuDangService.deleted(id));
    }

    @GetMapping("/get-list-style")
    public ResponseEntity<?> getListKieuDang() {
        return Helper.createResponseEntity(adKieuDangService.getListKieuDang());
    }

}
