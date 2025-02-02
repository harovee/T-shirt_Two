package com.shop.server.core.admin.anh_san_pham.service.impl;

import com.shop.server.core.admin.anh_san_pham.model.request.AdCreateUpdateAnhRequest;
import com.shop.server.core.admin.anh_san_pham.model.request.AdFindAnhRequest;
import com.shop.server.core.admin.anh_san_pham.repository.AdAnhRepository;
import com.shop.server.core.admin.anh_san_pham.service.AdAnhService;
import com.shop.server.core.admin.san_pham_chi_tiet.repository.AdSanPhamChiTietRepository;
import com.shop.server.core.common.base.PageableObject;
import com.shop.server.core.common.base.ResponseObject;
import com.shop.server.entities.main.Anh;
import com.shop.server.entities.main.DanhMuc;
import com.shop.server.repositories.SanPhamChiTietRepository;
import com.shop.server.utils.Helper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;


@Service
@RequiredArgsConstructor
public class AdAnhServiceImpl implements AdAnhService {

    private final AdAnhRepository adAnhRepository;

    private final AdSanPhamChiTietRepository adSanPhamChiTietRepository;

    @Override
    public ResponseObject<?> getAllAnhs(AdFindAnhRequest request) {
        return new ResponseObject<>(
                adAnhRepository.getAllAnh(request),
                HttpStatus.OK,
                "Lấy dữ liệu thành công."
        );
    }

    @Override
    public ResponseObject<?> createAnh(@Valid AdCreateUpdateAnhRequest request) {

//        if (request.getTen() == null || request.getTen().isBlank()) {
//            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên không được trống");
//        } else {
//            if (request.getTen().length() < 1 || request.getTen().length() > 255) {
//                return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên phải từ 1 -> 255 ký tự");
//            } else if (!checkName(request.getTen())) {
//                return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên không được chứa ký tự đặc biệt.");
//            } else if (adAnhRepository.existsDanhMucByTen(request.getTen())) {
//                return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên đã tồn tại, xin vui lòng nhập lại.");
//            }
//        }

//        Anh anh = new Anh();
//        anh.setTen(request.getTen());
//        anh.setUrl(request.getUrl());
//        anh.setSanPhamChiTiet(request.getIdSanPhamChiTiet() != null ? adSanPhamChiTietRepository.findById(request.getIdSanPhamChiTiet()).orElse(null) : null);
//        anh.setDeleted(false);
//        Anh anh1 = adAnhRepository.save(anh);
        return new ResponseObject<>(null, HttpStatus.CREATED, "Tạo ảnh thành công.");
    }

    @Override
    public ResponseObject<?> updateAnh(String id, @Valid AdCreateUpdateAnhRequest request) {

//        if (request.getTen() == null || request.getTen().isBlank()) {
//            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên không được trống");
//        } else {
//            if (request.getTen().length() < 1 || request.getTen().length() > 255) {
//                return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên phải từ 1 -> 255 ký tự");
//            } else if (!checkName(request.getTen())) {
//                return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên không được chứa ký tự đặc biệt.");
//            } else if (adAnhRepository.existsDanhMucByTenNotId(request.getTen(), id) != null) {
//                return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên đã tồn tại, xin vui lòng nhập lại.");
//            }
//        }

        Optional<Anh> anh = adAnhRepository.findById(id)
                .map(anh1 -> {
//                    anh1.setTen(request.getTen());
//                    anh1.setUrl(request.getUrl());
//                    anh1.setSanPhamChiTiet(request.getIdSanPhamChiTiet() != null ? adSanPhamChiTietRepository.findById(request.getIdSanPhamChiTiet()).orElse(null) : null);
                    return adAnhRepository.save(anh1);
                });
        return anh
                .map(anh1 -> new ResponseObject<>(anh1, HttpStatus.OK,
                        "Cập nhật ảnh thành công."))
                .orElseGet(() -> new ResponseObject<>(null, HttpStatus.NOT_FOUND,
                        "Ảnh không tồn tại."));
    }

    public boolean checkName(String name) {
        String regex = "^[a-zA-Z0-9àáãạảăắằẳẵặâấầẩẫậèéẹẻẽêềếểễệđìíĩỉịòóõọỏôốồổỗộơớờởỡợùúũụủư" +
                       "ứừửữựỳỵỷỹýÀÁÃẠẢĂẮẰẲẴẶÂẤẦẨẪẬÈÉẸẺẼÊỀẾỂỄỆĐÌÍĨỈỊÒÓÕỌỎÔỐỒỔỖỘƠỚỜỞỠỢÙÚŨỤỦƯỨỪỬỮỰỲỴỶỸÝ\\s]+$";
        return name != null && name.matches(regex);
    }
}
