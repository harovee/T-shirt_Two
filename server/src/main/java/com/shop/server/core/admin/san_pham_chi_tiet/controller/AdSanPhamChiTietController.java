package com.shop.server.core.admin.san_pham_chi_tiet.controller;

import com.shop.server.core.admin.san_pham_chi_tiet.model.request.AdCheckQuantityRequest;
import com.shop.server.core.admin.san_pham_chi_tiet.model.request.AdCreateUpdateSpctRequest;
import com.shop.server.core.admin.san_pham_chi_tiet.model.request.AdFindSpctRequest;
import com.shop.server.core.admin.san_pham_chi_tiet.model.response.AdSanPhamChiTietResponse;
import com.shop.server.core.admin.san_pham_chi_tiet.repository.AdSanPhamChiTietRepository;
import com.shop.server.core.admin.san_pham_chi_tiet.service.AdSanPhamChiTietService;
import com.shop.server.infrastructure.constants.module.MappingConstant;
import com.shop.server.utils.Helper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@RequestMapping(MappingConstant.API_ADMIN_PRODUCT_DETAIL)
@RequiredArgsConstructor
public class AdSanPhamChiTietController {

    private final AdSanPhamChiTietService adSanPhamChiTietService;
    private final AdSanPhamChiTietRepository adSanPhamChiTietRepository;

    @GetMapping()
    public ResponseEntity<?> getSanPhamChiTiet(@Valid final AdFindSpctRequest request) {
        return Helper.createResponseEntity(adSanPhamChiTietService.getSanPhamChiTiets(request));
    }

    @GetMapping("/all-product-detail")
    public ResponseEntity<?> getAllSanPhamChiTiet(@Valid final AdFindSpctRequest request) {
        return Helper.createResponseEntity(adSanPhamChiTietService.getALlSanPhamChiTiets(request));
    }

    @GetMapping("/all-product-detail-over-zero")
    public ResponseEntity<?> getAllSanPhamChiTietOverZero(@Valid final AdFindSpctRequest request) {
        return Helper.createResponseEntity(adSanPhamChiTietService.getALlSanPhamChiTietOverZero(request));
    }

    // Lấy danh sách toàn bộ SPCT  không phân trang
    @GetMapping("/list-product-detail")
    public ResponseEntity<?> getListSanPhamChiTiet() {
        return Helper.createResponseEntity(adSanPhamChiTietService.getListSanPhamChitiet());
    }


    // Check số lượng sản phẩm trong kho
    @GetMapping("/check-quantity")
    public ResponseEntity<?> checkQuantity(AdCheckQuantityRequest request) {
        return Helper.createResponseEntity(adSanPhamChiTietService.checkQuantity(request));
    }

    @GetMapping("/check-quantity/product-detail")
    public ResponseEntity<?> checkQuantityByIdSPCT(AdCheckQuantityRequest request) {
        return Helper.createResponseEntity(adSanPhamChiTietService.checkQuantityByIdSPCT(request));
    }

    @PostMapping("/check-quantity/list-product-detail")
    public ResponseEntity<?> checkQuantityInListProduct(@RequestBody List<AdCheckQuantityRequest> request) {
        return Helper.createResponseEntity(adSanPhamChiTietService.checkQuantityInListProduct(request));
    }

    @PutMapping("/delete-quantity")
    public ResponseEntity<?> deleteQuantityInListProduct(@RequestBody List<AdCheckQuantityRequest> request) {
        return Helper.createResponseEntity(adSanPhamChiTietService.deleteQuantityInStockByListProduct(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDetailSanPhamChiTiet(@PathVariable final String id) {
        return Helper.createResponseEntity(adSanPhamChiTietService.getSanPhamChiTietById(id));
    }

    @PostMapping()
    public ResponseEntity<?> createSanPhamChiTiet(@Valid @RequestBody final AdCreateUpdateSpctRequest request) {
        return Helper.createResponseEntity(adSanPhamChiTietService.createSanPhamChiTiet(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateSanPhamChiTiet(@PathVariable String id, @Valid @RequestBody final AdCreateUpdateSpctRequest request) {
        return Helper.createResponseEntity(adSanPhamChiTietService.updateSanPhamChiTiet(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSanPhamChiTiet(@PathVariable final String id) {
        return Helper.createResponseEntity(adSanPhamChiTietService.deleted(id));
    }
}
