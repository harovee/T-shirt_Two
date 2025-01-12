package com.shop.server.core.admin.bill.service;

import com.shop.server.core.admin.bill.model.request.AdminFindBillRequest;
import com.shop.server.core.common.base.ResponseObject;

public interface AdminBillService {

    ResponseObject<?> getBills(AdminFindBillRequest request);

    ResponseObject<?> getDetailBillById(String id);
}
