package com.shop.server.core.admin.kieu_dang.service;

import com.shop.server.core.admin.kieu_dang.model.request.AdCreateUpdateKieuDangRequest;
import com.shop.server.core.common.base.ResponseObject;
import jakarta.validation.Valid;

public interface AdKieuDangService {

    ResponseObject<?> getListKieuDang();

    ResponseObject<?> createKieuDang(@Valid AdCreateUpdateKieuDangRequest request);

}
