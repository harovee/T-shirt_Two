package com.shop.server.core.admin.phieugiamgia.controller;


import com.shop.server.core.admin.phieugiamgia.model.request.AdminKhachHangSearchRequest;
import com.shop.server.core.admin.phieugiamgia.model.request.AdVoucherKhachHangRequest;
import com.shop.server.core.admin.phieugiamgia.model.request.PhieuGiamGiaRequest;
import com.shop.server.core.admin.phieugiamgia.model.request.PhieuGiamGiaSearchRequest;
import com.shop.server.core.admin.phieugiamgia.services.Impl.AdPhieuGiamGiaServicesImpl;
import com.shop.server.infrastructure.constants.module.MappingConstant;
import com.shop.server.utils.GenBarcode;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@RequestMapping(MappingConstant.API_ADMIN_VOUCHER)
public class AdPhieuGiamGiaController {

    private final AdPhieuGiamGiaServicesImpl adPhieuGiamGiaServices;

    @GetMapping
    public ResponseEntity<?> getAdPhieuGiamGia(@Valid final PhieuGiamGiaSearchRequest request) {
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

    @PutMapping("/change-status/{id}/{trangThai}")
    public ResponseEntity<?> updateAdPhieuGiamGia(@PathVariable("id") String id,@PathVariable("trangThai") String trangThai) {
        return Helper.createResponseEntity(adPhieuGiamGiaServices.changeStatusPhieuGiamGia(id,trangThai));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAdPhieuGiamGia(@PathVariable String id) {
        return Helper.createResponseEntity(adPhieuGiamGiaServices.deletePhieuGiamGiaById(id));
    }

    @GetMapping("/khach-hang")
    public ResponseEntity<?> getKhachHang(@Valid final AdminKhachHangSearchRequest request) {
        return Helper.createResponseEntity(adPhieuGiamGiaServices.getAllKhachHang(request));
    }

    @GetMapping("/khach-hang/{id}")
    public ResponseEntity<?> getKhachHangByIdPhieuGiamGia(@PathVariable("id") String id) {
        return Helper.createResponseEntity(adPhieuGiamGiaServices.getKhachHangByIdPhieuGiamGia(id));
    }

    @PostMapping("/save-voucher-khach-hang")
    public ResponseEntity<?> addVoucherSanPhamKhachHang(@Valid final @RequestBody AdVoucherKhachHangRequest request){
        return Helper.createResponseEntity(adPhieuGiamGiaServices.createVoucherKhachHang(request.getPhieuGiamGiaRequest(), request.getVoucherKhachHangRequest()));
    }
    @PutMapping("/save-voucher-khach-hang/{id}")
    public ResponseEntity<?> updateVoucherSanPhamKhachHang(@PathVariable String id,@Valid final @RequestBody AdVoucherKhachHangRequest request){
        return Helper.createResponseEntity(adPhieuGiamGiaServices.updateVoucherKhachHang(id,request));
    }

    @GetMapping("/check-voucher-in-use/{id}")
    public ResponseEntity<?> checkVoucherInUse(@PathVariable String id) {
        return Helper.createResponseEntity(adPhieuGiamGiaServices.checkVoucherInUse(id));
    }
}
