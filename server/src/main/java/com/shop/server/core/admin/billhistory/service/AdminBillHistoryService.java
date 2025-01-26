package com.shop.server.core.admin.billhistory.service;

import com.shop.server.core.admin.billhistory.model.request.AdminCreateHistoryRequest;
import com.shop.server.core.admin.billhistory.model.request.AdminFindBillHistoryRequest;
import com.shop.server.core.admin.billhistory.model.response.AdminBillHistoryResponse;
import com.shop.server.core.common.base.ResponseObject;

public interface AdminBillHistoryService {
    ResponseObject<?> getAdminBillHistory(AdminFindBillHistoryRequest request);


    ResponseObject<?> createAdminBillHistory(AdminCreateHistoryRequest request);
}
