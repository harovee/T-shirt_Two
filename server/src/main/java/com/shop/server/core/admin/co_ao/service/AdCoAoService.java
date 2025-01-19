package com.shop.server.core.admin.co_ao.service;

import com.shop.server.core.admin.co_ao.model.request.AdCreateUpdateCoAoRequest;
import com.shop.server.core.admin.co_ao.model.request.AdFindCoAoRequest;
import com.shop.server.core.common.base.ResponseObject;
import jakarta.validation.Valid;

public interface AdCoAoService {

    ResponseObject<?> getAllCoAos(AdFindCoAoRequest request);

    ResponseObject<?> getCoAo(String id);

    ResponseObject<?> createCoAo(@Valid AdCreateUpdateCoAoRequest request);

    ResponseObject<?> updateCoAo(String id, @Valid AdCreateUpdateCoAoRequest request);

    ResponseObject<?> deleted(String id);

    ResponseObject<?> getListCoAo();

}
