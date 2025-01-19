package com.shop.server.core.admin.mau_sac.service;

import com.shop.server.core.admin.mau_sac.model.request.AdCreateUpdateMauSacRequest;
import com.shop.server.core.admin.mau_sac.model.request.AdFindMauSacRequest;
import com.shop.server.core.common.base.ResponseObject;
import jakarta.validation.Valid;

public interface AdMauSacService {

    ResponseObject<?> getAllMauSacs(AdFindMauSacRequest request);

    ResponseObject<?> getMauSac(String id);

    ResponseObject<?> createMauSac(@Valid AdCreateUpdateMauSacRequest request);

    ResponseObject<?> updateMauSac(String id, @Valid AdCreateUpdateMauSacRequest request);

    ResponseObject<?> deleted(String id);

    ResponseObject<?> getListMauSac();

}
