package com.shop.server.core.admin.tinh_nang.controller;

import com.shop.server.core.admin.tinh_nang.model.request.AdCreateUpdateTinhNangRequest;
import com.shop.server.core.admin.tinh_nang.model.request.AdFindTinhNangRequest;
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

    @GetMapping()
    public ResponseEntity<?> getAllTinhNangs(@Valid final AdFindTinhNangRequest request) {
        return Helper.createResponseEntity(adTinhNangService.getAllTinhNangs(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTinhNangDetail(@PathVariable final String id) {
        return Helper.createResponseEntity(adTinhNangService.getTinhNang(id));
    }

    @PostMapping()
    public ResponseEntity<?> createTinhNang(@Valid @RequestBody final AdCreateUpdateTinhNangRequest request) {
        return Helper.createResponseEntity(adTinhNangService.createTinhNang(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTinhNang(@PathVariable String id, @Valid @RequestBody final AdCreateUpdateTinhNangRequest request) {
        return Helper.createResponseEntity(adTinhNangService.updateTinhNang(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTinhNang(@PathVariable String id) {
        return Helper.createResponseEntity(adTinhNangService.deleted(id));
    }

    @GetMapping("/get-list-feature")
    public ResponseEntity<?> getListTinhNang() {
        return Helper.createResponseEntity(adTinhNangService.getListTinhNang());
    }

}
