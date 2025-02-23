package com.shop.server.core.admin.staff.services;

import com.shop.server.core.admin.staff.models.requests.AdminFindStaffRequest;
import com.shop.server.core.admin.staff.models.requests.AdminStaffQRRequest;
import com.shop.server.core.admin.staff.models.requests.AdminStaffRequest;
import com.shop.server.core.admin.staff.models.responses.AdminStaffExcelResponse;
import com.shop.server.core.common.base.ResponseObject;
import jakarta.validation.Valid;

import java.util.List;

public interface AdminStaffService {

    ResponseObject<?> getStaffs(AdminFindStaffRequest request);

    ResponseObject<?> getStaffById(String id);

    ResponseObject<?> createStaff(@Valid AdminStaffRequest request);

    ResponseObject<?> createStaffByQRCode(@Valid AdminStaffQRRequest request);

    ResponseObject<?> updateStaff(String id, @Valid AdminStaffRequest request);

    ResponseObject<?> changeStatusStaff(String id);

    ResponseObject<?> updateStaffAvatar(String id, AdminStaffRequest request);

    List<AdminStaffExcelResponse> getStaffsExcel();

}
