package com.shop.server.core.admin.tay_ao.controller;

import com.shop.server.core.admin.tay_ao.model.request.AdCreateUpdateTayAoRequest;
import com.shop.server.core.admin.tay_ao.model.request.AdFindTayAoRequest;
import com.shop.server.core.admin.tay_ao.service.AdTayAoService;
import com.shop.server.infrastructure.constants.module.MappingConstant;
import com.shop.server.utils.Helper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping(MappingConstant.API_ADMIN_SLEEVE)
@RestController
@RequiredArgsConstructor
public class AdTayAoController {

    private final AdTayAoService adTayAoService;

    @GetMapping()
    public ResponseEntity<?> getAllTayAos(@Valid final AdFindTayAoRequest request) {
        return Helper.createResponseEntity(adTayAoService.getAllTayAos(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTayAoDetail(@PathVariable final String id) {
        return Helper.createResponseEntity(adTayAoService.getTayAo(id));
    }

    @PostMapping()
    public ResponseEntity<?> createTayAo(@Valid @RequestBody final AdCreateUpdateTayAoRequest request) {
        return Helper.createResponseEntity(adTayAoService.createTayAo(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTayAo(@PathVariable String id, @Valid @RequestBody final AdCreateUpdateTayAoRequest request) {
        return Helper.createResponseEntity(adTayAoService.updateTayAo(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTayAo(@PathVariable String id) {
        return Helper.createResponseEntity(adTayAoService.deleted(id));
    }

    @GetMapping("/get-list-sleeve")
    public ResponseEntity<?> getListTayAo() {
        return Helper.createResponseEntity(adTayAoService.getListTayAo());
    }

}
