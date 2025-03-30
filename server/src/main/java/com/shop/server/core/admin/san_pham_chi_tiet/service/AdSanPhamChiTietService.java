package com.shop.server.core.admin.san_pham_chi_tiet.service;

import com.shop.server.core.admin.san_pham_chi_tiet.model.request.AdCheckQuantityRequest;
import com.shop.server.core.admin.san_pham_chi_tiet.model.request.AdCreateUpdateSpctRequest;
import com.shop.server.core.admin.san_pham_chi_tiet.model.request.AdFindSpctRequest;
import com.shop.server.core.common.base.ResponseObject;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

@Service
public interface AdSanPhamChiTietService {

    ResponseObject<?> getSanPhamChiTiets(AdFindSpctRequest request);

    ResponseObject<?> getALlSanPhamChiTiets(AdFindSpctRequest request);

    ResponseObject<?> getALlSanPhamChiTietOverZero(AdFindSpctRequest request);

    ResponseObject<?> getListSanPhamChitiet();

    ResponseObject<?> getSanPhamChiTietById(String id);

    ResponseObject<?> createSanPhamChiTiet(@Valid AdCreateUpdateSpctRequest request);

    ResponseObject<?> updateSanPhamChiTiet(String id, @Valid AdCreateUpdateSpctRequest request);

    ResponseObject<?> deleted(String id);

    ResponseObject<?> checkQuantity (AdCheckQuantityRequest request);
}
