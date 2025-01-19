package com.shop.server.core.admin.co_ao.controller;

import com.shop.server.core.admin.co_ao.model.request.AdCreateUpdateCoAoRequest;
import com.shop.server.core.admin.co_ao.model.request.AdFindCoAoRequest;
import com.shop.server.core.admin.co_ao.service.AdCoAoService;
import com.shop.server.infrastructure.constants.module.MappingConstant;
import com.shop.server.utils.Helper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping(MappingConstant.API_ADMIN_COLLAR)
@RestController
@RequiredArgsConstructor
public class AdCoAoController {

    private final AdCoAoService adCoAoService;

    @GetMapping()
    public ResponseEntity<?> getAllCoAos(@Valid final AdFindCoAoRequest request) {
        return Helper.createResponseEntity(adCoAoService.getAllCoAos(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCoAoDetail(@PathVariable final String id) {
        return Helper.createResponseEntity(adCoAoService.getCoAo(id));
    }

    @PostMapping()
    public ResponseEntity<?> createCoAo(@Valid @RequestBody final AdCreateUpdateCoAoRequest request) {
        return Helper.createResponseEntity(adCoAoService.createCoAo(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCoAo(@PathVariable String id, @Valid @RequestBody final AdCreateUpdateCoAoRequest request) {
        return Helper.createResponseEntity(adCoAoService.updateCoAo(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCoAo(@PathVariable String id) {
        return Helper.createResponseEntity(adCoAoService.deleted(id));
    }

    @GetMapping("/get-list-collar")
    public ResponseEntity<?> getListCoAo() {
        return Helper.createResponseEntity(adCoAoService.getListCoAo());
    }

}
