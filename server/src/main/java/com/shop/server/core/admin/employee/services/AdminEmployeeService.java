package com.shop.server.core.admin.employee.services;

import com.shop.server.core.admin.employee.models.requests.EmployeeFindProductRequest;
import com.shop.server.core.admin.employee.models.requests.EmployeeProductRequest;
import com.shop.server.core.common.base.ResponseObject;
import jakarta.validation.Valid;

public interface AdminEmployeeService {

    ResponseObject<?> getEmployees(EmployeeFindProductRequest request);

    ResponseObject<?> getEmployeeById(String id);

    ResponseObject<?> createEmployee(@Valid EmployeeProductRequest request);

    ResponseObject<?> updateEmployee(String id, @Valid EmployeeProductRequest request);

    ResponseObject<?> changeStatusEmployee(String id);


}
