package com.shop.server.core.admin.tinh_nang.service;

import com.shop.server.core.admin.tinh_nang.model.request.AdCreateUpdateTinhNangRequest;
import com.shop.server.core.admin.tinh_nang.model.request.AdFindTinhNangRequest;
import com.shop.server.core.common.base.ResponseObject;
import jakarta.validation.Valid;

public interface AdTinhNangService {

    ResponseObject<?> getAllTinhNangs(AdFindTinhNangRequest request);

    ResponseObject<?> getTinhNang(String id);

    ResponseObject<?> createTinhNang(@Valid AdCreateUpdateTinhNangRequest request);

    ResponseObject<?> updateTinhNang(String id, @Valid AdCreateUpdateTinhNangRequest request);

    ResponseObject<?> deleted(String id);

    ResponseObject<?> getListTinhNang();

}
