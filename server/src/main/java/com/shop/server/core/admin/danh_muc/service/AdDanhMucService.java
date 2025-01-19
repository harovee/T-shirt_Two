package com.shop.server.core.admin.danh_muc.service;

import com.shop.server.core.admin.danh_muc.model.request.AdCreateUpdateDanhMucRequest;
import com.shop.server.core.admin.danh_muc.model.request.AdFindDanhMucRequest;
import com.shop.server.core.common.base.ResponseObject;
import jakarta.validation.Valid;

public interface AdDanhMucService {

    ResponseObject<?> getAllDanhMucs(AdFindDanhMucRequest request);

    ResponseObject<?> getDanhMuc(String id);

    ResponseObject<?> createDanhMuc(@Valid AdCreateUpdateDanhMucRequest request);

    ResponseObject<?> updateDanhMuc(String id, @Valid AdCreateUpdateDanhMucRequest request);

    ResponseObject<?> deleted(String id);

    ResponseObject<?> getListDanhMuc();

}
