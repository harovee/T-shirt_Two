package com.shop.server.core.client.product.controller;

import com.shop.server.core.client.product.model.request.ClientProductDetailRequest;
import com.shop.server.core.client.product.model.request.ClientProductRequest;
import com.shop.server.core.client.product.model.request.ClientProductSearchRequest;
import com.shop.server.core.client.product.services.ClientProductService;
import com.shop.server.core.client.product.services.ClientThuocTinhServices;
import com.shop.server.infrastructure.constants.module.MappingConstant;
import com.shop.server.utils.Helper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(MappingConstant.API_CLIENT_PRODUCT)
@CrossOrigin("*")
@RequiredArgsConstructor
public class ClientProductController {
    private final ClientProductService clientProductService;

    private final ClientThuocTinhServices clientThuocTinhServices;

    @GetMapping
    public ResponseEntity<?> getAllProducts(@Valid final ClientProductSearchRequest request) {
        return Helper.createResponseEntity(clientProductService.getAllProducts(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProduct(@PathVariable final String id,@Valid final ClientProductRequest request) {
        return Helper.createResponseEntity(clientProductService.getProductById(id, request));
    }
    @GetMapping("/moi-nhat")
    public ResponseEntity<?> getProductMoiNhat(@Valid final ClientProductSearchRequest request) {
        return Helper.createResponseEntity(clientProductService.getTop8Product(request));
    }
    @GetMapping("/detail/{id}")
    public ResponseEntity<?> getProductDetailById(@PathVariable String id, @Valid final ClientProductDetailRequest request) {
        return Helper.createResponseEntity(clientProductService.getProductDetaiById(id,request));
    }

    @GetMapping("/danh-muc")
    public ResponseEntity<?> getProductDanhMuc() {
        return  Helper.createResponseEntity(clientThuocTinhServices.getDanhMuc());
    }

    @GetMapping("/chat-lieu")
    public ResponseEntity<?> getProductChatLieu() {
        return  Helper.createResponseEntity(clientThuocTinhServices.getChatLieu());
    }

    @GetMapping("/thuong-hieu")
    public ResponseEntity<?> getProductThuongHieu() {
        return  Helper.createResponseEntity(clientThuocTinhServices.getThuongHieu());
    }

    @GetMapping("/color")
    public ResponseEntity<?> getProductMauSac() {
        return  Helper.createResponseEntity(clientThuocTinhServices.getColor());
    }

    @GetMapping("/kieu-dang")
    public ResponseEntity<?> getProductKieuDang() {
        return  Helper.createResponseEntity(clientThuocTinhServices.getKieuDang());
    }
}
