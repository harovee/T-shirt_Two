package com.shop.server.core.admin.phieugiamgia.controller;


import com.shop.server.core.admin.phieugiamgia.model.request.AdminKhachHangSearchRequest;
import com.shop.server.core.admin.phieugiamgia.model.request.AdminProductSearchRequest;
import com.shop.server.core.admin.phieugiamgia.model.request.AdminVoucherSanPhamKhachHangRequest;
import com.shop.server.core.admin.phieugiamgia.model.request.PhieuGiamGiaRequest;
import com.shop.server.core.admin.phieugiamgia.model.request.PhieuGiamGiaSearchRequest;
import com.shop.server.core.admin.phieugiamgia.services.Impl.AdPhieuGiamGiaServicesImpl;
import com.shop.server.infrastructure.constants.module.MappingConstant;
import com.shop.server.utils.Helper;
import jakarta.validation.Valid;
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
@CrossOrigin(origins = "*")
@RequestMapping(MappingConstant.API_ADMIN_VOUCHER)
public class AdPhieuGiamGiaController {

    private final AdPhieuGiamGiaServicesImpl adPhieuGiamGiaServices;

    @GetMapping
    public ResponseEntity<?> getAdPhieuGiamGia(PhieuGiamGiaSearchRequest request) {
        return Helper.createResponseEntity(adPhieuGiamGiaServices.getAllPhieuGiamGia(request));
    }

    @PostMapping
    public ResponseEntity<?> addAdPhieuGiamGia(@Valid final @RequestBody PhieuGiamGiaRequest request) {
        return Helper.createResponseEntity(adPhieuGiamGiaServices.createPhieuGiamGia(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateAdPhieuGiamGia(@Valid final @RequestBody PhieuGiamGiaRequest request,@PathVariable("id") String id) {
        return Helper.createResponseEntity(adPhieuGiamGiaServices.updatePhieuGiamGiaById(request,id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAdPhieuGiamGiaById(@PathVariable String id) {
        return Helper.createResponseEntity(adPhieuGiamGiaServices.getPhieuGiamGiaById(id));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAdPhieuGiamGia(@PathVariable String id) {
        return Helper.createResponseEntity(adPhieuGiamGiaServices.deletePhieuGiamGiaById(id));
    }
    @GetMapping("/san_pham")
    public ResponseEntity<?> getSanPham(@Valid AdminProductSearchRequest request) {
        return Helper.createResponseEntity(adPhieuGiamGiaServices.getAllProductDetail(request));
    }
    @GetMapping("/san_pham/{id}")
    public ResponseEntity<?> getSanPham(@PathVariable("idSanPham") String id) {
        return Helper.createResponseEntity(adPhieuGiamGiaServices.getProductDetailById(id));
    }
    @GetMapping("/khach_hang")
    public ResponseEntity<?> getKhachHang(@Valid AdminKhachHangSearchRequest request) {
        return Helper.createResponseEntity(adPhieuGiamGiaServices.getAllKhachHang(request));
    }
    @GetMapping("/khach_hang/{id}")
    public ResponseEntity<?> getKhachHang(@PathVariable("idKhachHang") String id) {
        return Helper.createResponseEntity(adPhieuGiamGiaServices.getKhachHangById(id));
    }

    @PostMapping("/save_voucher-san_pham_khach_hang")
    public ResponseEntity<?> addVoucherSanPhamKhachHang(@Valid final @RequestBody AdminVoucherSanPhamKhachHangRequest request){
        System.out.println(request.getVoucherSanPhamKhachHangRequest().getIdVoucher());
        return Helper.createResponseEntity(adPhieuGiamGiaServices.createVoucherSanphamKhachHang(request.getPhieuGiamGiaRequest(), request.getVoucherSanPhamKhachHangRequest()));
    }
}
