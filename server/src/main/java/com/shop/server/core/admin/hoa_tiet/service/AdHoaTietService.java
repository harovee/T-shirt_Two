package com.shop.server.core.admin.hoa_tiet.service;

import com.shop.server.core.admin.hoa_tiet.model.request.AdCreateUpdateHoaTietRequest;
import com.shop.server.core.common.base.ResponseObject;
import jakarta.validation.Valid;

public interface AdHoaTietService {

    ResponseObject<?> getListHoaTiet();

    ResponseObject<?> createHoaTiet(@Valid AdCreateUpdateHoaTietRequest request);

}
