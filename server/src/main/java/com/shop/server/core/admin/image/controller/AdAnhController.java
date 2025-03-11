package com.shop.server.core.admin.image.controller;

import com.shop.server.core.admin.image.model.request.AdCreateUpdateAnhRequest;
import com.shop.server.core.admin.image.model.request.AdFindAnhRequest;
import com.shop.server.core.admin.image.service.AdAnhService;
import com.shop.server.infrastructure.constants.module.MappingConstant;
import com.shop.server.utils.Helper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping(MappingConstant.API_ADMIN_IMAGE)
@RestController
@RequiredArgsConstructor
public class AdAnhController {

    private final AdAnhService adAnhService;

    @GetMapping()
    public ResponseEntity<?> getAllAnhs(@Valid final AdFindAnhRequest request) {
        return Helper.createResponseEntity(adAnhService.getAllAnhs(request));
    }

//    @GetMapping("/{id}")
//    public ResponseEntity<?> getDanhMucDetail(@PathVariable final String id) {
//        return Helper.createResponseEntity(adAnhService.getDanhMuc(id));
//    }

    @PostMapping()
    public ResponseEntity<?> createAnh(@Valid @RequestBody final AdCreateUpdateAnhRequest request) {
        return Helper.createResponseEntity(adAnhService.createAnh(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateAnh(@PathVariable String id, @Valid @RequestBody final AdCreateUpdateAnhRequest request) {
        return Helper.createResponseEntity(adAnhService.updateAnh(id, request));
    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<?> deleteDanhMuc(@PathVariable String id) {
//        return Helper.createResponseEntity(adAnhService.deleted(id));
//    }
//
//    @GetMapping("/get-list-category")
//    public ResponseEntity<?> getListDanhMuc() {
//        return Helper.createResponseEntity(adAnhService.getListDanhMuc());
//    }

}
