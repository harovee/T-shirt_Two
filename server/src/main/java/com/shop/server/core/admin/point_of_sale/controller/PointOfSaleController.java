package com.shop.server.core.admin.point_of_sale.controller;


import com.shop.server.core.admin.point_of_sale.model.request.AdPOSFindProductRequest;
import com.shop.server.core.admin.point_of_sale.service.PointOfSaleServiceIml;
import com.shop.server.infrastructure.constants.module.MappingConstant;
import com.shop.server.utils.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(MappingConstant.API_ADMIN_POINT_SALE)
public class PointOfSaleController {

    private final PointOfSaleServiceIml pointOfSaleService;

    @GetMapping("/products")
    public ResponseEntity<?> getProducts(final AdPOSFindProductRequest adFindProductRequest) {
        return Helper.createResponseEntity(pointOfSaleService.getProductsInPOS());
    }


}
