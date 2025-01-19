package com.shop.server.core.admin.kieu_dang.service;

import com.shop.server.core.admin.kieu_dang.model.request.AdCreateUpdateKieuDangRequest;
import com.shop.server.core.admin.kieu_dang.model.request.AdFindKieuDangRequest;
import com.shop.server.core.common.base.ResponseObject;
import jakarta.validation.Valid;

public interface AdKieuDangService {

    ResponseObject<?> getAllKieuDangs(AdFindKieuDangRequest request);

    ResponseObject<?> getKieuDang(String id);

    ResponseObject<?> createKieuDang(@Valid AdCreateUpdateKieuDangRequest request);

    ResponseObject<?> updateKieuDang(String id, @Valid AdCreateUpdateKieuDangRequest request);

    ResponseObject<?> deleted(String id);

    ResponseObject<?> getListKieuDang();

}
