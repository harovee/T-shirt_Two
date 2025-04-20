package com.shop.server.core.admin.client.controllers;

import com.shop.server.core.admin.client.models.requests.AdminClientAddressMoRequest;
import com.shop.server.core.admin.client.models.requests.AdminClientAddressRequest;
import com.shop.server.core.admin.client.models.requests.AdminClientRequest;
import com.shop.server.core.admin.client.models.requests.AdminFindClientRequest;
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
    public ResponseEntity<?> getClients(@Valid final AdminFindClientRequest request) {
        return Helper.createResponseEntity(adminClientService.getClients(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getClientById(@PathVariable String id) {
        return Helper.createResponseEntity(adminClientService.getClientById(id));
    }

    @PostMapping()
    public ResponseEntity<?> createClient(@Valid @RequestBody final AdminClientRequest request) {
        return Helper.createResponseEntity(adminClientService.createClient(request));
    }

    @PostMapping("/mo")
    public ResponseEntity<?> createClientMo(@Valid @RequestBody final AdminClientAddressMoRequest request) {
        return Helper.createResponseEntity(adminClientService.createClientMo(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateClient(@PathVariable String id, @Valid @RequestBody final AdminClientRequest request) {
        return Helper.createResponseEntity(adminClientService.updateClient(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteClient(@PathVariable String id) {
        return Helper.createResponseEntity(adminClientService.changeStatusClient(id));
    }

    @PutMapping("/avatar/{id}")
    public ResponseEntity<?> updateClientAvatar(@PathVariable String id, @RequestBody final AdminClientRequest request) {
        return Helper.createResponseEntity(adminClientService.updateClientAvatar(id, request));
    }

    // ** Filter province - district - ward ** \\

    @GetMapping("/province")
    public ResponseEntity<?> getProvinces() {
        return Helper.createResponseEntity(adminClientService.getProvinces());
    }

    @GetMapping("/district/{id}")
    public ResponseEntity<?> getDistrictsByProvinceId(@PathVariable Long id) {
        return Helper.createResponseEntity(adminClientService.getDistrictsByProvinceId(id));
    }

    @GetMapping("/ward/{id}")
    public ResponseEntity<?> getWardsByProvinceId(@PathVariable Long id) {
        return Helper.createResponseEntity(adminClientService.getWardsByDistrictId(id));
    }

    @GetMapping("/address/{id}")
    public ResponseEntity<?> getClientAddressesByClientId(@PathVariable String id) {
        return Helper.createResponseEntity(adminClientService.getAddressesByClientId(id));
    }

    @PostMapping("/address")
    public ResponseEntity<?> createClientAddress(@RequestBody final AdminClientAddressRequest request) {
        return Helper.createResponseEntity(adminClientService.createClientAddress(request));
    }

    @PutMapping("/address/{id}")
    public ResponseEntity<?> updateClientAddress(@PathVariable String id, @RequestBody final AdminClientAddressRequest request) {
        return Helper.createResponseEntity(adminClientService.updateClientAddress(id, request));
    }

    @PutMapping("/address/default/{id}")
    public ResponseEntity<?> updateClientAddressDefault(@PathVariable String id) {
        return Helper.createResponseEntity(adminClientService.changeDefaultClientAddress(id));
    }

    @GetMapping("/client-chat-list")
    public ResponseEntity<?> getClientsInChat() {
        return Helper.createResponseEntity(adminClientService.getClientListInChat());
    }
}
