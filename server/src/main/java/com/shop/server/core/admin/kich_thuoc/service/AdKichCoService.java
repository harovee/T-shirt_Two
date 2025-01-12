package com.shop.server.core.admin.kich_thuoc.service;

import com.shop.server.core.admin.kich_thuoc.model.request.AdCreateUpdateKichCoRequest;
import com.shop.server.core.common.base.ResponseObject;
import jakarta.validation.Valid;

public interface AdKichCoService {

    ResponseObject<?> getListKichCo();

    ResponseObject<?> createKichCo(@Valid AdCreateUpdateKichCoRequest request);

}
