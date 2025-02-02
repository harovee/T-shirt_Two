package com.shop.server.core.admin.anh_san_pham.service;

import com.shop.server.core.admin.anh_san_pham.model.request.AdCreateUpdateAnhRequest;
import com.shop.server.core.admin.anh_san_pham.model.request.AdFindAnhRequest;
import com.shop.server.core.common.base.ResponseObject;
import jakarta.validation.Valid;

public interface AdAnhService {

    ResponseObject<?> getAllAnhs(AdFindAnhRequest request);

    ResponseObject<?> createAnh(@Valid AdCreateUpdateAnhRequest request);

    ResponseObject<?> updateAnh(String id, @Valid AdCreateUpdateAnhRequest request);

}
