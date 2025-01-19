package com.shop.server.core.admin.sale.controllers;

import com.shop.server.core.admin.sale.models.requests.AdminFindProductDetailSaleModuleRequest;
import com.shop.server.core.admin.sale.models.requests.AdminFindProductSaleModuleRequest;
import com.shop.server.core.admin.sale.models.requests.AdminFindSaleProductDetailRequest;
import com.shop.server.core.admin.sale.models.requests.AdminFindSaleRequest;
import com.shop.server.core.admin.sale.models.requests.AdminSaleAndSaleProductDetailRequest;
import com.shop.server.core.admin.sale.models.requests.AdminSaleProductRequest;
import com.shop.server.core.admin.sale.models.requests.AdminSaleRequest;
import com.shop.server.core.admin.sale.services.AdminSaleService;
import com.shop.server.infrastructure.constants.module.MappingConstant;
import com.shop.server.utils.Helper;
import jakarta.validation.Valid;
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

@RequestMapping(MappingConstant.API_ADMIN_PROMOTION)
@RestController
@CrossOrigin("*")
public class AdminSaleController {

    private final AdminSaleService adminSaleService;

    public AdminSaleController(AdminSaleService adminSaleService) {
        this.adminSaleService = adminSaleService;
    }


    @GetMapping()
    public ResponseEntity<?> getSales(@Valid final AdminFindSaleRequest request) {
        return Helper.createResponseEntity(adminSaleService.getSales(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDetailSale(@PathVariable String id) {
        return Helper.createResponseEntity(adminSaleService.getSaleById(id));
    }

    @PostMapping()
    public ResponseEntity<?> createSale(@Valid @RequestBody final AdminSaleRequest request) {
        return Helper.createResponseEntity(adminSaleService.createSale(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateSale(@PathVariable String id, @Valid @RequestBody final AdminSaleRequest request) {
        return Helper.createResponseEntity(adminSaleService.updateSale(id, request));
    }

    @PutMapping("/change-status/{id}/{status}")
    public ResponseEntity<?> updateSaleStatus(@PathVariable String id, @PathVariable String status) {
        return Helper.createResponseEntity(adminSaleService.changeStatusSale(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSale(@PathVariable String id) {
        return Helper.createResponseEntity(adminSaleService.deleteSale(id));
    }

    @GetMapping("/product-detail-attributes")
    public ResponseEntity<?> getAttributes() {
        return Helper.createResponseEntity(adminSaleService.mapListAttributesOfProductDetail());
    }

    @GetMapping("/products")
    public ResponseEntity<?> getProducts(final AdminFindProductSaleModuleRequest request) {
        return Helper.createResponseEntity(adminSaleService.getProducts(request));
    }

    @GetMapping("/product-details")
    public ResponseEntity<?> getProductDetails(final AdminFindProductDetailSaleModuleRequest request) {
        return Helper.createResponseEntity(adminSaleService.getProductDetails(request));
    }

    @GetMapping("/sale-product-details")
    public ResponseEntity<?> getSaleProductDetails(final AdminFindSaleProductDetailRequest request) {
        return Helper.createResponseEntity(adminSaleService.getSaleProductDetailBySaleId(request));
    }

    @PostMapping("/sale-product-details")
    public ResponseEntity<?> saveSaleProductDetails(@Valid @RequestBody final AdminSaleProductRequest request) {
        return Helper.createResponseEntity(adminSaleService.saveSaleProductDetails(request));
    }

    @PostMapping("/save-sale-and-sale-product-details")
    public ResponseEntity<?> saveSaleAndSaleProductDetails(@Valid @RequestBody final AdminSaleAndSaleProductDetailRequest request) {
        return Helper.createResponseEntity(adminSaleService.saveSaleInfoAndSaleProductDetails(
                request.getSaleRequest(),
                request.getSaleProductRequest()));
    }

    @PostMapping("/save-sale-and-sale-product-details/{id}")
    public ResponseEntity<?> saveSaleAndSaleProductDetails(
            @PathVariable("id") String id,
            @Valid @RequestBody final AdminSaleAndSaleProductDetailRequest request) {
        return Helper.createResponseEntity(adminSaleService.updateSaleAndSaveSaleProduct(id, request));
    }

    @DeleteMapping("/sale-product-details/{id}")
    public ResponseEntity<?> deleteSaleProductById(@PathVariable String id) {
        return Helper.createResponseEntity(adminSaleService.deleteSaleProductById(id));
    }


}
