package com.shop.server.core.client.product.services;

import com.shop.server.core.client.product.model.request.ClientProductDetailRequest;
import com.shop.server.core.client.product.model.request.ClientProductSearchRequest;
import com.shop.server.core.common.base.ResponseObject;
import org.springframework.stereotype.Service;

@Service
public interface ClientProductService {
    ResponseObject<?> getAllProducts(ClientProductSearchRequest request);

    ResponseObject<?> getProductById(String idSanPham, ClientProductDetailRequest request);

    ResponseObject<?> getTop8Product(ClientProductSearchRequest request);

    ResponseObject<?> getProductDetaiById(String idSanPham,ClientProductDetailRequest request);
}
