package com.shop.server.core.admin.kich_co.controller;

import com.shop.server.core.admin.kich_co.model.request.AdCreateUpdateKichCoRequest;
import com.shop.server.core.admin.kich_co.model.request.AdFindKichCoRequest;
import com.shop.server.core.admin.kich_co.service.AdKichCoService;
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

    @GetMapping()
    public ResponseEntity<?> getAllKichCos(@Valid final AdFindKichCoRequest request) {
        return Helper.createResponseEntity(adKichCoService.getAllKichCos(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getKichCoDetail(@PathVariable final String id) {
        return Helper.createResponseEntity(adKichCoService.getKichCo(id));
    }

    @PostMapping()
    public ResponseEntity<?> createKichCo(@Valid @RequestBody final AdCreateUpdateKichCoRequest request) {
        return Helper.createResponseEntity(adKichCoService.createKichCo(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateKichCo(@PathVariable String id, @Valid @RequestBody final AdCreateUpdateKichCoRequest request) {
        return Helper.createResponseEntity(adKichCoService.updateKichCo(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteKichCo(@PathVariable String id) {
        return Helper.createResponseEntity(adKichCoService.deleted(id));
    }

    @GetMapping("/get-list-size")
    public ResponseEntity<?> getListKichCo() {
        return Helper.createResponseEntity(adKichCoService.getListKichCo());
    }

}
