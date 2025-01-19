package com.shop.server.core.admin.mau_sac.controller;

import com.shop.server.core.admin.mau_sac.model.request.AdCreateUpdateMauSacRequest;
import com.shop.server.core.admin.mau_sac.model.request.AdFindMauSacRequest;
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

    @GetMapping()
    public ResponseEntity<?> getAllMauSacs(@Valid final AdFindMauSacRequest request) {
        return Helper.createResponseEntity(adMauSacService.getAllMauSacs(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMauSacDetail(@PathVariable final String id) {
        return Helper.createResponseEntity(adMauSacService.getMauSac(id));
    }

    @PostMapping()
    public ResponseEntity<?> createMauSac(@Valid @RequestBody final AdCreateUpdateMauSacRequest request) {
        return Helper.createResponseEntity(adMauSacService.createMauSac(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateMauSac(@PathVariable String id, @Valid @RequestBody final AdCreateUpdateMauSacRequest request) {
        return Helper.createResponseEntity(adMauSacService.updateMauSac(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMauSac(@PathVariable String id) {
        return Helper.createResponseEntity(adMauSacService.deleted(id));
    }

    @GetMapping("/get-list-color")
    public ResponseEntity<?> getListMauSac() {
        return Helper.createResponseEntity(adMauSacService.getListMauSac());
    }

}
