package com.shop.server.core.admin.product.controllers;

import com.shop.server.core.admin.product.models.requests.AdminFindProductRequest;
import com.shop.server.core.admin.product.models.requests.AdminProductRequest;
import com.shop.server.core.admin.product.services.AdminProductService;
import com.shop.server.infrastructure.constants.module.MappingConstant;
import com.shop.server.utils.Helper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(MappingConstant.API_ADMIN_PRODUCT)
@RestController
public class AdminProductController {

    private final AdminProductService adminProductService;

    public AdminProductController(AdminProductService adminProductService) {
        this.adminProductService = adminProductService;
    }


    @GetMapping()
    public ResponseEntity<?> getProducts(@Valid final AdminFindProductRequest request) {
        return Helper.createResponseEntity(adminProductService.getProducts(request));
    }

    @PostMapping()
    public ResponseEntity<?> createProduct(@Valid @RequestBody final AdminProductRequest request) {
        return Helper.createResponseEntity(adminProductService.createProduct(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable String id, @Valid @RequestBody final AdminProductRequest request) {
        return Helper.createResponseEntity(adminProductService.updateProduct(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable String id) {
        return Helper.createResponseEntity(adminProductService.changeStatusProduct(id));
    }

}
