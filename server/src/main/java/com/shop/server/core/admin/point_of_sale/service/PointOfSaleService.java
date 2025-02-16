package com.shop.server.core.admin.point_of_sale.service;

import com.shop.server.core.admin.point_of_sale.model.request.AdPOSAddProductsToCartRequest;
import com.shop.server.core.admin.point_of_sale.model.request.AdPOSFindProductRequest;
import com.shop.server.core.admin.point_of_sale.model.request.AdPOSUpdateCartRequest;
import com.shop.server.core.common.base.ResponseObject;

public interface PointOfSaleService {

    ResponseObject<?> getProductsInPOS(AdPOSFindProductRequest request);

    ResponseObject<?> addProductsToCart(AdPOSAddProductsToCartRequest request);

    ResponseObject<?> getProductsInPendingOrder(String idOrder);

    Object getPriceRank();

    ResponseObject<?> updateQuantityProductInCart(AdPOSUpdateCartRequest request);

    ResponseObject<?> deleteOrderDetail(String idHDCT);

    ResponseObject<?> getTotalAmount(String idHoaDon);

    ResponseObject<?> getListProducts(String idHoaDon);

}
