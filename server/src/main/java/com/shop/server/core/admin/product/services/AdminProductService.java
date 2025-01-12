package com.shop.server.core.admin.product.services;

import com.shop.server.core.admin.product.models.requests.AdGetSanPhamChiTietRequest;
import com.shop.server.core.admin.product.models.requests.AdminFindProductRequest;
import com.shop.server.core.admin.product.models.requests.AdminProductRequest;
import com.shop.server.core.common.base.ResponseObject;
import jakarta.validation.Valid;

public interface AdminProductService {

    ResponseObject<?> getProducts(AdminFindProductRequest request);

    ResponseObject<?> getProductById(String id);

    ResponseObject<?> createProduct(@Valid AdminProductRequest request);

    ResponseObject<?> updateProduct(String id, @Valid AdminProductRequest request);

    ResponseObject<?> deleted(String id);

    ResponseObject<?> getListProduct();

    ResponseObject<?> getSanPhamChiTietByIdSanPham(String id, AdGetSanPhamChiTietRequest request);

}
