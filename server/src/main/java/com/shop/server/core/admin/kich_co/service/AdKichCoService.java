package com.shop.server.core.admin.kich_co.service;

import com.shop.server.core.admin.kich_co.model.request.AdCreateUpdateKichCoRequest;
import com.shop.server.core.admin.kich_co.model.request.AdFindKichCoRequest;
import com.shop.server.core.common.base.ResponseObject;
import jakarta.validation.Valid;

public interface AdKichCoService {

    ResponseObject<?> getAllKichCos(AdFindKichCoRequest request);

    ResponseObject<?> getKichCo(String id);

    ResponseObject<?> createKichCo(@Valid AdCreateUpdateKichCoRequest request);

    ResponseObject<?> updateKichCo(String id, @Valid AdCreateUpdateKichCoRequest request);

    ResponseObject<?> deleted(String id);

    ResponseObject<?> getListKichCo();

}
