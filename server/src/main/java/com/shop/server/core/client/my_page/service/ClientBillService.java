package com.shop.server.core.client.my_page.service;

import com.shop.server.core.client.my_page.model.request.ClientBillRequest;
import com.shop.server.core.common.base.ResponseObject;

public interface ClientBillService {

    ResponseObject<?> getAllBill (ClientBillRequest request);
}
