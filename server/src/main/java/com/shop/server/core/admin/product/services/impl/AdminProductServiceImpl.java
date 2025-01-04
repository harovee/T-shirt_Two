package com.shop.server.core.admin.product.services.impl;

import com.shop.server.core.admin.product.models.requests.AdminFindProductRequest;
import com.shop.server.core.admin.product.models.requests.AdminProductRequest;
import com.shop.server.core.admin.product.repositories.AdminProductRepository;
import com.shop.server.core.admin.product.services.AdminProductService;
import com.shop.server.core.common.base.PageableObject;
import com.shop.server.core.common.base.PageableRequest;
import com.shop.server.core.common.base.ResponseObject;
import com.shop.server.infrastructure.constants.module.Message;
import com.shop.server.utils.Helper;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;


@Service
public class AdminProductServiceImpl implements AdminProductService {

    private final AdminProductRepository adminProductRepository;

    public AdminProductServiceImpl(AdminProductRepository adminProductRepository) {
        this.adminProductRepository = adminProductRepository;
    }

    @Override
    public ResponseObject<?> getProducts(AdminFindProductRequest request) {
        Pageable pageable = Helper.createPageable(request);
        return new ResponseObject<>(
                PageableObject.of(adminProductRepository.getProductsByRequest(pageable, request)),
                HttpStatus.OK,
                Message.Success.GET_SUCCESS
        );
    }

    @Override
    public ResponseObject<?> getProductById(String id) {
        return null;
    }

    @Override
    public ResponseObject<?> createProduct(AdminProductRequest request) {
        return null;
    }

    @Override
    public ResponseObject<?> updateProduct(String id, AdminProductRequest request) {
        return null;
    }

    @Override
    public ResponseObject<?> changeStatusProduct(String id) {
        return null;
    }
}
