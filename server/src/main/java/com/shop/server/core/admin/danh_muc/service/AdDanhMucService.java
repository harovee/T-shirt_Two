package com.shop.server.core.admin.danh_muc.service;

import com.shop.server.core.admin.danh_muc.model.request.AdCreateUpdateDanhMucRequest;
import com.shop.server.core.common.base.ResponseObject;
import jakarta.validation.Valid;

public interface AdDanhMucService {

    ResponseObject<?> getListDanhMuc();

    ResponseObject<?> createDanhMuc(@Valid AdCreateUpdateDanhMucRequest request);

}
