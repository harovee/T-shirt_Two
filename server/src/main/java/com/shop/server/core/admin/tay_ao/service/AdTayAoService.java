package com.shop.server.core.admin.tay_ao.service;

import com.shop.server.core.admin.tay_ao.model.request.AdCreateUpdateTayAoRequest;
import com.shop.server.core.common.base.ResponseObject;
import jakarta.validation.Valid;

public interface AdTayAoService {

    ResponseObject<?> getListTayAo();

    ResponseObject<?> createTayAo(@Valid AdCreateUpdateTayAoRequest request);

}
