package com.shop.server.core.admin.thuong_hieu.service;

import com.shop.server.core.admin.thuong_hieu.model.request.AdCreateUpdateThuongHieuRequest;
import com.shop.server.core.common.base.ResponseObject;
import jakarta.validation.Valid;

public interface AdThuongHieuService {

    ResponseObject<?> getListThuongHieu();

    ResponseObject<?> createThuongHieu(@Valid AdCreateUpdateThuongHieuRequest request);

}
