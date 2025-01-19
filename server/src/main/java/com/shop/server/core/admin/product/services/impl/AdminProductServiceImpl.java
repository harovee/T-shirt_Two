package com.shop.server.core.admin.product.services.impl;

import com.shop.server.core.admin.danh_muc.repository.AdDanhMucRepository;
import com.shop.server.core.admin.product.models.requests.AdGetSanPhamChiTietRequest;
import com.shop.server.core.admin.product.models.requests.AdminFindProductRequest;
import com.shop.server.core.admin.product.models.requests.AdminProductRequest;
import com.shop.server.core.admin.product.repositories.AdminProductRepository;
import com.shop.server.core.admin.product.services.AdminProductService;
import com.shop.server.core.common.base.PageableObject;
import com.shop.server.core.common.base.ResponseObject;
import com.shop.server.entities.main.SanPham;
import com.shop.server.infrastructure.constants.module.Status;
import com.shop.server.utils.Helper;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;


@Service
@RequiredArgsConstructor
public class AdminProductServiceImpl implements AdminProductService {

    private final AdminProductRepository adminProductRepository;

    private final AdDanhMucRepository adDanhMucRepository;

    @Override
    public ResponseObject<?> getProducts(AdminFindProductRequest request) {
        Pageable pageable = Helper.createPageable(request);
        return new ResponseObject<>(
                PageableObject.of(adminProductRepository.getProductsByRequest(pageable, request)),
                HttpStatus.OK,
                "Lấy dữ liệu thành công."
        );
    }

    @Override
    public ResponseObject<?> getProductById(String id) {

        return adminProductRepository.findById(id)
                .map(ok -> new ResponseObject<>(ok, HttpStatus.OK, "Lấy thông tin sản phẩm thành công."))
                .orElseGet(() -> new ResponseObject<>(null, HttpStatus.NOT_FOUND,
                        "Id: " + id + " không tồn tại"));
    }

    @Override
    public ResponseObject<?> getSanPhamChiTietByIdSanPham(String id, AdGetSanPhamChiTietRequest request) {
        Pageable pageable = Helper.createPageable(request);
        return new ResponseObject<>(
                PageableObject.of(adminProductRepository.getSanPhamChiTietByIdSanPham(pageable, id)),
                HttpStatus.OK,
                "Lấy dữ liệu thành công."
        );
    }

    @Override
    public ResponseObject<?> createProduct(AdminProductRequest request) {

        if (request.getTen() == null || request.getTen().isBlank()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên không được trống");
        } else {
            if (request.getTen().length() < 1 || request.getTen().length() > 255) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên phải từ 1 -> 255 ký tự");
            } else if (!checkName(request.getTen())) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên không được chứa ký tự đặc biệt.");
            } else if (adminProductRepository.existsSanPhamByTen(request.getTen())) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên đã tồn tại, xin vui lòng nhập lại.");
            }
        }

        SanPham sanPham = new SanPham();
        Random random = new Random();
        String code;
        do {
            int number = random.nextInt(1000);
            code = String.format("SP%04d", number);
        } while (adminProductRepository.existsSanPhamByMaSanPham(code));
        sanPham.setMaSanPham(code);
        sanPham.setTen(request.getTen());
        sanPham.setMoTa(request.getMoTa());
        sanPham.setTrangThai(Status.ACTIVE);
        sanPham.setDanhMuc(adDanhMucRepository.findById(request.getIdDanhMuc()).orElse(null));
        sanPham.setDeleted(false);
        SanPham addedSanPham = adminProductRepository.save(sanPham);
        return new ResponseObject<>(addedSanPham, HttpStatus.CREATED, "Tạo sản phẩm thành công.");
    }

    @Override
    public ResponseObject<?> updateProduct(String id, AdminProductRequest request) {

        if (request.getTen() == null || request.getTen().isBlank()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên không được trống");
        } else {
            if (request.getTen().length() < 1 || request.getTen().length() > 255) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên phải từ 1 -> 255 ký tự");
            } else if (!checkName(request.getTen())) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên không được chứa ký tự đặc biệt.");
            } else if (adminProductRepository.existsSanPhamByNameAndIdNotEx(request.getTen(), id) != null) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên đã tồn tại, xin vui lòng nhập lại.");
            }
        }

        Optional<SanPham> sanPham = adminProductRepository.findById(id)
                .map(sanPham1 -> {
                    sanPham1.setTen(request.getTen());
                    sanPham1.setMoTa(request.getMoTa());
                    sanPham1.setDanhMuc(adDanhMucRepository.findById(request.getIdDanhMuc()).orElse(null));
                    sanPham1.setTrangThai(request.getTrangThai() == 0 ? Status.ACTIVE : Status.INACTIVE);
                    return adminProductRepository.save(sanPham1);
                });
        return sanPham
                .map(sanPham1 -> new ResponseObject<>(sanPham1, HttpStatus.OK,
                        "Cập nhật sản phẩm thành công."))
                .orElseGet(() -> new ResponseObject<>(null, HttpStatus.NOT_FOUND,
                        "Sản phẩm không tồn tại."));
    }

    @Override
    public ResponseObject<?> deleted(String id) {
        Optional<SanPham> sanPham = adminProductRepository.findById(id)
                .map(sanPham1 -> {
                    sanPham1.setDeleted(true);
                    return adminProductRepository.save(sanPham1);
                });
        return sanPham
                .map(sanPham1 -> new ResponseObject<>(null, HttpStatus.OK,
                        "Xóa sản phẩm thành công."))
                .orElseGet(() -> new ResponseObject<>(null, HttpStatus.NOT_FOUND,
                        "Sản phẩm không tồn tại."));
    }

    @Override
    public ResponseObject<?> getListProduct() {
        return new ResponseObject<>(adminProductRepository.getListProduct(),
                HttpStatus.OK,
                "Lấy dữ liệu thành công."
        );
    }

    public boolean checkName(String name) {
        String regex = "^[a-zA-Z0-9àáãạảăắằẳẵặâấầẩẫậèéẹẻẽêềếểễệđìíĩỉịòóõọỏôốồổỗộơớờởỡợùúũụủư" +
                       "ứừửữựỳỵỷỹýÀÁÃẠẢĂẮẰẲẴẶÂẤẦẨẪẬÈÉẸẺẼÊỀẾỂỄỆĐÌÍĨỈỊÒÓÕỌỎÔỐỒỔỖỘƠỚỜỞỠỢÙÚŨỤỦƯỨỪỬỮỰỲỴỶỸÝ\\s]+$";
        return name != null && name.matches(regex);
    }
}
