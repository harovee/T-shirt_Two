package com.shop.server.core.admin.staff.controllers;

import com.shop.server.core.admin.staff.models.requests.AdminFindStaffRequest;
import com.shop.server.core.admin.staff.models.requests.AdminStaffRequest;
import com.shop.server.core.admin.staff.services.AdminStaffService;
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

@RequestMapping(MappingConstant.API_ADMIN_STAFF)
@RestController
public class AdminStaffController {

    private final AdminStaffService adminStaffService;

    public AdminStaffController(AdminStaffService adminStaffService) {
        this.adminStaffService = adminStaffService;
    }

    @GetMapping()
    public ResponseEntity<?> getStaffs(@Valid final AdminFindStaffRequest request) {
        return Helper.createResponseEntity(adminStaffService.getStaffs(request));
    }

    @PostMapping()
    public ResponseEntity<?> createStaff(@Valid @RequestBody final AdminStaffRequest request) {
        return Helper.createResponseEntity(adminStaffService.createStaff(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateStaff(@PathVariable String id, @Valid @RequestBody final AdminStaffRequest request) {
        return Helper.createResponseEntity(adminStaffService.updateStaff(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStaff(@PathVariable String id) {
        return Helper.createResponseEntity(adminStaffService.changeStatusStaff(id));
    }

}
