package com.shop.server.core.admin.tinh_nang.service;

import com.shop.server.core.admin.tinh_nang.model.request.AdCreateUpdateTinhNangRequest;
import com.shop.server.core.common.base.ResponseObject;
import jakarta.validation.Valid;

public interface AdTinhNangService {

    ResponseObject<?> getListTinhNang();

    ResponseObject<?> createTinhNang(@Valid AdCreateUpdateTinhNangRequest request);

}
