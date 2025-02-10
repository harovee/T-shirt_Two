package com.shop.server.core.admin.point_of_sale.controller;

import com.shop.server.core.admin.point_of_sale.model.request.AdPOSAddProductsToCartRequest;
import com.shop.server.core.admin.point_of_sale.model.request.AdPOSFindProductRequest;
import com.shop.server.core.admin.point_of_sale.model.request.AdPOSUpdateCartRequest;
import com.shop.server.core.admin.point_of_sale.service.PointOfSaleServiceIml;
import com.shop.server.infrastructure.constants.module.MappingConstant;
import com.shop.server.utils.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(MappingConstant.API_ADMIN_POINT_SALE)
@CrossOrigin("*")
public class PointOfSaleController {

    private final PointOfSaleServiceIml pointOfSaleService;

    @GetMapping("/products")
    public ResponseEntity<?> getProducts(final AdPOSFindProductRequest adFindProductRequest) {
        return Helper.createResponseEntity(pointOfSaleService.getProductsInPOS(adFindProductRequest));
    }

    @GetMapping("/sub/price-rank")
    public Object getPriceRank() {
        return pointOfSaleService.getPriceRank();
    }

    @GetMapping("/products-in-order/{id}")
    public ResponseEntity<?> getProductsInOrder(@PathVariable("id") String idOrder) {
        return Helper.createResponseEntity(pointOfSaleService.getProductsInPendingOrder(idOrder));
    }

    @PostMapping("/products-in-order")
    public ResponseEntity<?> addProductsToOrder(@RequestBody AdPOSAddProductsToCartRequest request) {
        return Helper.createResponseEntity(pointOfSaleService.addProductsToCart(request));
    }

    @PutMapping("/products-in-order")
    public ResponseEntity<?> updateQuantityProductsToOrder(@RequestBody AdPOSUpdateCartRequest request) {
        return Helper.createResponseEntity(pointOfSaleService.updateQuantityProductInCart(request));
    }

    @DeleteMapping("/products-in-order/{idHDCT}")
    public ResponseEntity<?> addProductsToOrder(@PathVariable("idHDCT") String idHDCT) {
        return Helper.createResponseEntity(pointOfSaleService.deleteOrderDetail(idHDCT));
    }
}
