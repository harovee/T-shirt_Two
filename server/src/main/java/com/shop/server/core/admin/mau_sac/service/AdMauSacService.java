package com.shop.server.core.admin.mau_sac.service;

import com.shop.server.core.admin.mau_sac.model.request.AdCreateUpdateMauSacRequest;
import com.shop.server.core.common.base.ResponseObject;
import jakarta.validation.Valid;

public interface AdMauSacService {

    ResponseObject<?> getListMauSac();

    ResponseObject<?> createMauSac(@Valid AdCreateUpdateMauSacRequest request);

}
