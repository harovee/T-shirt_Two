package com.shop.server.core.admin.bill.service;

import com.shop.server.core.admin.bill.model.request.AdminFindBillRequest;
import com.shop.server.core.admin.bill.model.request.AdminSaveBillRequest;
import com.shop.server.core.admin.bill.model.request.AdminUpdateBillRequest;
import com.shop.server.core.common.base.ResponseObject;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;

public interface AdminBillService {

    ResponseObject<?> getBills(AdminFindBillRequest request);

    ResponseObject<?> getDetailBillById(String id);

    ResponseObject<?> createBill(@Valid AdminSaveBillRequest request, BindingResult bindingResult);

    ResponseObject<?> updateBill(String id, @Valid AdminUpdateBillRequest request, BindingResult bindingResult);

    ResponseObject<?> changeStatusBill(String id, AdminUpdateBillRequest request);
}
