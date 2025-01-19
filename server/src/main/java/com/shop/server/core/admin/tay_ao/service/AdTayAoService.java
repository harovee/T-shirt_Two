package com.shop.server.core.admin.tay_ao.service;

import com.shop.server.core.admin.tay_ao.model.request.AdCreateUpdateTayAoRequest;
import com.shop.server.core.admin.tay_ao.model.request.AdFindTayAoRequest;
import com.shop.server.core.common.base.ResponseObject;
import jakarta.validation.Valid;

public interface AdTayAoService {

    ResponseObject<?> getAllTayAos(AdFindTayAoRequest request);

    ResponseObject<?> getTayAo(String id);

    ResponseObject<?> createTayAo(@Valid AdCreateUpdateTayAoRequest request);

    ResponseObject<?> updateTayAo(String id, @Valid AdCreateUpdateTayAoRequest request);

    ResponseObject<?> deleted(String id);

    ResponseObject<?> getListTayAo();

}
