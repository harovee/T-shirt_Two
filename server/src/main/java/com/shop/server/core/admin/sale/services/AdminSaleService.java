package com.shop.server.core.admin.sale.services;

import com.shop.server.core.admin.sale.models.requests.AdminFindProductDetailSaleModuleRequest;
import com.shop.server.core.admin.sale.models.requests.AdminFindProductSaleModuleRequest;
import com.shop.server.core.admin.sale.models.requests.AdminFindSaleProductDetailRequest;
import com.shop.server.core.admin.sale.models.requests.AdminFindSaleRequest;
import com.shop.server.core.admin.sale.models.requests.AdminSaleAndSaleProductDetailRequest;
import com.shop.server.core.admin.sale.models.requests.AdminSaleProductRequest;
import com.shop.server.core.admin.sale.models.requests.AdminSaleRequest;
import com.shop.server.core.common.base.ResponseObject;
import jakarta.validation.Valid;

public interface AdminSaleService {

    ResponseObject<?> getSales(AdminFindSaleRequest request);

    ResponseObject<?> getSaleById(String id);

    ResponseObject<?> createSale(@Valid AdminSaleRequest request);

    ResponseObject<?> updateSale(String id, @Valid AdminSaleRequest request);

    ResponseObject<?> changeStatusSale(String id, String status);

    ResponseObject<?> deleteSale(String id);

    ResponseObject<?> mapListAttributesOfProductDetail();

    ResponseObject<?> getProductDetails(AdminFindProductDetailSaleModuleRequest request);

    ResponseObject<?> getProducts(AdminFindProductSaleModuleRequest request);

    ResponseObject<?> saveSaleProductDetails(AdminSaleProductRequest request);

    ResponseObject<?> saveSaleInfoAndSaleProductDetails(
            AdminSaleRequest saleRequest,
            AdminSaleProductRequest saleProductRequest);

    ResponseObject<?> getSaleProductDetailBySaleId(AdminFindSaleProductDetailRequest request);

    ResponseObject<?> deleteSaleProductById(String saleProductId);

    ResponseObject<?> updateSaleAndSaveSaleProduct(String saleId, AdminSaleAndSaleProductDetailRequest request);


}
