package com.shop.server.core.admin.client.controllers;

import com.shop.server.core.admin.client.models.requests.ClientFindProductRequest;
import com.shop.server.core.admin.client.models.requests.ClientProductRequest;
import com.shop.server.core.admin.client.services.AdminClientService;
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

@RequestMapping(MappingConstant.API_ADMIN_CLIENT)
@RestController
public class AdminClientController {

    private final AdminClientService adminClientService;

    public AdminClientController(AdminClientService adminClientService) {
        this.adminClientService = adminClientService;
    }


    @GetMapping()
    public ResponseEntity<?> getProducts(@Valid final ClientFindProductRequest request) {
        return Helper.createResponseEntity(adminClientService.getClients(request));
    }

    @PostMapping()
    public ResponseEntity<?> createProduct(@Valid @RequestBody final ClientProductRequest request) {
        return Helper.createResponseEntity(adminClientService.createClient(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable String id, @Valid @RequestBody final ClientProductRequest request) {
        return Helper.createResponseEntity(adminClientService.updateClient(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable String id) {
        return Helper.createResponseEntity(adminClientService.changeStatusClient(id));
    }

}
