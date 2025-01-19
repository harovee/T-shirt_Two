package com.shop.server.core.admin.thuong_hieu.service;

import com.shop.server.core.admin.thuong_hieu.model.request.AdCreateUpdateThuongHieuRequest;
import com.shop.server.core.admin.thuong_hieu.model.request.AdFindThuongHieuRequest;
import com.shop.server.core.common.base.ResponseObject;
import jakarta.validation.Valid;

public interface AdThuongHieuService {

    ResponseObject<?> getAllThuongHieus(AdFindThuongHieuRequest request);

    ResponseObject<?> getThuongHieu(String id);

    ResponseObject<?> createThuongHieu(@Valid AdCreateUpdateThuongHieuRequest request);

    ResponseObject<?> updateThuongHieu(String id, @Valid AdCreateUpdateThuongHieuRequest request);

    ResponseObject<?> deleted(String id);

    ResponseObject<?> getListThuongHieu();

}
