package com.shop.server.core.admin.billhistory.service;

import com.shop.server.core.admin.billhistory.model.request.AdminFindPayHistoryRequest;
import com.shop.server.core.common.base.ResponseObject;

public interface AdminPayHistoryService {
    ResponseObject<?> getPayHistory(AdminFindPayHistoryRequest request);
}
