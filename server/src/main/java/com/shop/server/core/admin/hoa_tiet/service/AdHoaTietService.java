package com.shop.server.core.admin.hoa_tiet.service;

import com.shop.server.core.admin.hoa_tiet.model.request.AdCreateUpdateHoaTietRequest;
import com.shop.server.core.admin.hoa_tiet.model.request.AdFindHoaTietRequest;
import com.shop.server.core.common.base.ResponseObject;
import jakarta.validation.Valid;

public interface AdHoaTietService {

    ResponseObject<?> getAllHoaTiets(AdFindHoaTietRequest request);

    ResponseObject<?> getHoaTiet(String id);

    ResponseObject<?> createHoaTiet(@Valid AdCreateUpdateHoaTietRequest request);

    ResponseObject<?> updateHoaTiet(String id, @Valid AdCreateUpdateHoaTietRequest request);

    ResponseObject<?> deleted(String id);

    ResponseObject<?> getListHoaTiet();

}
