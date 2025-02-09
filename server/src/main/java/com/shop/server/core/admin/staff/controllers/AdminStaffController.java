package com.shop.server.core.admin.staff.controllers;

import com.shop.server.core.admin.staff.models.requests.AdminFindStaffRequest;
import com.shop.server.core.admin.staff.models.requests.AdminStaffRequest;
import com.shop.server.core.admin.staff.models.responses.AdminStaffExcelResponse;
import com.shop.server.core.admin.staff.services.AdminStaffExcelService;
import com.shop.server.core.admin.staff.services.AdminStaffService;
import com.shop.server.core.common.base.ResponseObject;
import com.shop.server.infrastructure.constants.module.MappingConstant;
import com.shop.server.utils.Helper;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.List;

@RequestMapping(MappingConstant.API_ADMIN_STAFF)
@RestController
public class AdminStaffController {

    private final AdminStaffService adminStaffService;

    private final AdminStaffExcelService adminStaffExcelService;

    public AdminStaffController(AdminStaffService adminStaffService, AdminStaffExcelService adminStaffExcelService) {
        this.adminStaffService = adminStaffService;
        this.adminStaffExcelService = adminStaffExcelService;
    }

    @GetMapping()
    public ResponseEntity<?> getStaffs(@Valid final AdminFindStaffRequest request) {
        return Helper.createResponseEntity(adminStaffService.getStaffs(request));
    }

    @PostMapping()
    public ResponseEntity<?> createStaff(@Valid @RequestBody final AdminStaffRequest request) {
        return Helper.createResponseEntity(adminStaffService.createStaff(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getStaffById(@PathVariable String id) {
        return Helper.createResponseEntity(adminStaffService.getStaffById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateStaff(@PathVariable String id, @Valid @RequestBody final AdminStaffRequest request) {
        return Helper.createResponseEntity(adminStaffService.updateStaff(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStaff(@PathVariable String id) {
        return Helper.createResponseEntity(adminStaffService.changeStatusStaff(id));
    }

    @PutMapping("/avatar/{id}")
    public ResponseEntity<?> updateStaffAvatar(@PathVariable String id, @RequestBody final AdminStaffRequest request) {
        return Helper.createResponseEntity(adminStaffService.updateStaffAvatar(id, request));
    }

    @GetMapping("/export-excel")
    public ResponseEntity<?> exportExcel() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "nhan_vien.xlsx");
        List<AdminStaffExcelResponse> staffs = adminStaffService.getStaffsExcel();

        ResponseObject<?> response = adminStaffExcelService.exportExcel(staffs);

        if (response.getData() instanceof ByteArrayInputStream) {
            return new ResponseEntity<>(((ByteArrayInputStream) response.getData()).readAllBytes(), headers, HttpStatus.OK);
        }

        return Helper.createResponseEntity(response);
    }

    @GetMapping("/export-template-excel")
    public ResponseEntity<?> exportTemplateExcel() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "template_import_nhan_vien.xlsx");
        ResponseObject<?> response = adminStaffExcelService.exportTemplateExcel();
        if (response.getData() instanceof ByteArrayInputStream) {
            return new ResponseEntity<>(((ByteArrayInputStream) response.getData()).readAllBytes(), headers, HttpStatus.OK);
        }

        return Helper.createResponseEntity(response);
    }

    @PostMapping("/import-excel")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        return Helper.createResponseEntity(adminStaffExcelService.importExcel(file));
    }

}
