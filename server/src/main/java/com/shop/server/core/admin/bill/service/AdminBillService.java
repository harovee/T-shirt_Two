package com.shop.server.core.admin.bill.service;

import com.shop.server.core.admin.bill.model.request.*;
import com.shop.server.core.common.base.ResponseObject;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;

import java.util.Map;

public interface AdminBillService {

    ResponseObject<?> getBills(AdminFindBillRequest request);

    ResponseObject<?> getBillsWait();

    ResponseObject<?> getDetailBillById(String id);

    ResponseObject<?> getBillRefundByMaHD(String maHoaDon);

//    ResponseObject<?> createBill(@Valid AdminSaveBillRequest request, BindingResult bindingResult);

    ResponseObject<?> createBill(AdminSaveBillRequest request);

    ResponseObject<?> updateBill(String id, @Valid AdminUpdateBillRequest request, BindingResult bindingResult);

    ResponseObject<?> updateBillConfirm(String id, @Valid AdminUpdateBillConfirmRequest request);

    ResponseObject<?> changeStatusBill(String id, AdminUpdateBillRequest request);

    Map<String, Integer> getBillCountsByStatus();

    ResponseObject<?> removeBillWait(String id);

    ResponseObject<?> updateBillWait(String id, AdminUpdateBillWaitRequest request);


}
