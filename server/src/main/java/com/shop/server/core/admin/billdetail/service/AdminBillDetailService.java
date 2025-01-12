package com.shop.server.core.admin.billdetail.service;

import com.shop.server.core.admin.billdetail.model.request.AdminCreateBillDetailRequest;
import com.shop.server.core.admin.billdetail.model.request.AdminFindBillDetailRequest;
import com.shop.server.core.admin.billdetail.model.request.AdminUpdateBillDetailRequest;
import com.shop.server.core.common.base.ResponseObject;
import jakarta.validation.Valid;

public interface AdminBillDetailService {
    ResponseObject<?> getBillDetailsByRequest(AdminFindBillDetailRequest request);

    ResponseObject<?> getBillDetailById(String id);

    ResponseObject<?> createBillDetail(@Valid AdminCreateBillDetailRequest request);

    ResponseObject<?> updateBillDetail(String id, @Valid AdminUpdateBillDetailRequest request);
}
