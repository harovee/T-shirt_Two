package com.shop.server.core.admin.billdetail.service;

import com.shop.server.core.admin.billdetail.model.request.AdminFindBillDetailRequest;
import com.shop.server.core.common.base.ResponseObject;

public interface AdminBillDetailService {
    ResponseObject<?> getBillDetailsByRequest(AdminFindBillDetailRequest request);

    ResponseObject<?> getBillDetailById(String id);
}
