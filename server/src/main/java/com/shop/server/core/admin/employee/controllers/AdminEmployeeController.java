package com.shop.server.core.admin.employee.controllers;

import com.shop.server.core.admin.employee.models.requests.EmployeeFindProductRequest;
import com.shop.server.core.admin.employee.models.requests.EmployeeProductRequest;
import com.shop.server.core.admin.employee.services.AdminEmployeeService;
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

@RequestMapping(MappingConstant.API_ADMIN_EMPLOYEE)
@RestController
public class AdminEmployeeController {

    private final AdminEmployeeService adminEmployeeService;

    public AdminEmployeeController(AdminEmployeeService adminEmployeeService) {
        this.adminEmployeeService = adminEmployeeService;
    }


    @GetMapping()
    public ResponseEntity<?> getProducts(@Valid final EmployeeFindProductRequest request) {
        return Helper.createResponseEntity(adminEmployeeService.getEmployees(request));
    }

    @PostMapping()
    public ResponseEntity<?> createProduct(@Valid @RequestBody final EmployeeProductRequest request) {
        return Helper.createResponseEntity(adminEmployeeService.createEmployee(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable String id, @Valid @RequestBody final EmployeeProductRequest request) {
        return Helper.createResponseEntity(adminEmployeeService.updateEmployee(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable String id) {
        return Helper.createResponseEntity(adminEmployeeService.changeStatusEmployee(id));
    }

}
