package com.shop.server.core.admin.staff.services;

import com.shop.server.core.admin.staff.models.responses.AdminStaffExcelResponse;
import com.shop.server.core.common.base.ResponseObject;

import java.util.List;

public interface AdminStaffExcelService {

    ResponseObject<?> exportExcel(List<AdminStaffExcelResponse> staffs);

}
