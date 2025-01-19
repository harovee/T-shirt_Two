package com.shop.server.core.admin.hoa_tiet.controller;

import com.shop.server.core.admin.hoa_tiet.model.request.AdCreateUpdateHoaTietRequest;
import com.shop.server.core.admin.hoa_tiet.model.request.AdFindHoaTietRequest;
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

    @GetMapping()
    public ResponseEntity<?> getAllHoaTiets(@Valid final AdFindHoaTietRequest request) {
        return Helper.createResponseEntity(adHoaTietService.getAllHoaTiets(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getHoaTietDetail(@PathVariable final String id) {
        return Helper.createResponseEntity(adHoaTietService.getHoaTiet(id));
    }

    @PostMapping()
    public ResponseEntity<?> createHoaTiet(@Valid @RequestBody final AdCreateUpdateHoaTietRequest request) {
        return Helper.createResponseEntity(adHoaTietService.createHoaTiet(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateHoaTiet(@PathVariable String id, @Valid @RequestBody final AdCreateUpdateHoaTietRequest request) {
        return Helper.createResponseEntity(adHoaTietService.updateHoaTiet(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteHoaTiet(@PathVariable String id) {
        return Helper.createResponseEntity(adHoaTietService.deleted(id));
    }

    @GetMapping("/get-list-pattern")
    public ResponseEntity<?> getListHoaTiet() {
        return Helper.createResponseEntity(adHoaTietService.getListHoaTiet());
    }

}
