package com.shop.server.core.admin.danh_muc.service.impl;

import com.shop.server.core.admin.danh_muc.model.request.AdCreateUpdateDanhMucRequest;
import com.shop.server.core.admin.danh_muc.model.request.AdFindDanhMucRequest;
import com.shop.server.core.admin.danh_muc.repository.AdDanhMucRepository;
import com.shop.server.core.admin.danh_muc.service.AdDanhMucService;
import com.shop.server.core.common.base.PageableObject;
import com.shop.server.core.common.base.ResponseObject;
import com.shop.server.entities.main.DanhMuc;
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
public class AdDanhMucServiceImpl implements AdDanhMucService {

    private final AdDanhMucRepository adDanhMucRepository;

    @Override
    public ResponseObject<?> getAllDanhMucs(AdFindDanhMucRequest request) {
        Pageable pageable = Helper.createPageable(request);

        return new ResponseObject<>(
                PageableObject.of(adDanhMucRepository.getAllDanhMucs(pageable, request)),
                HttpStatus.OK,
                "Lấy dữ liệu thành công."
        );
    }

    @Override
    public ResponseObject<?> getDanhMuc(String id) {

        return adDanhMucRepository.findById(id)
                .map(ok -> new ResponseObject<>(ok, HttpStatus.OK, "Lấy thông tin danh mục thành công."))
                .orElseGet(() -> new ResponseObject<>(null, HttpStatus.NOT_FOUND,
                        "Id: " + id + " không tồn tại"));
    }

    @Override
    public ResponseObject<?> getListDanhMuc() {
        return new ResponseObject<>(adDanhMucRepository.getListDanhMuc(),
                HttpStatus.OK,
                "Lấy dữ liệu thành công."
        );
    }

    @Override
    public ResponseObject<?> createDanhMuc(@Valid AdCreateUpdateDanhMucRequest request) {

        if (request.getTen() == null || request.getTen().isBlank()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên không được trống");
        } else {
            if (request.getTen().length() < 1 || request.getTen().length() > 255) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên phải từ 1 -> 255 ký tự");
            } else if (!checkName(request.getTen())) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên không được chứa ký tự đặc biệt.");
            } else if (adDanhMucRepository.existsDanhMucByTen(request.getTen())) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên đã tồn tại, xin vui lòng nhập lại.");
            }
        }

        DanhMuc DanhMuc = new DanhMuc();
        Random random = new Random();
        String code;
        do {
            int number = random.nextInt(1000);
            code = String.format("DM%04d", number);
        } while (adDanhMucRepository.existsDanhMucByMaDanhMuc(code));
        DanhMuc.setMaDanhMuc(code);
        DanhMuc.setTen(request.getTen());
        DanhMuc.setDeleted(false);
        DanhMuc addedDanhMuc = adDanhMucRepository.save(DanhMuc);
        return new ResponseObject<>(addedDanhMuc, HttpStatus.CREATED, "Tạo danh mục thành công.");
    }

    @Override
    public ResponseObject<?> updateDanhMuc(String id, @Valid AdCreateUpdateDanhMucRequest request) {

        if (request.getTen() == null || request.getTen().isBlank()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên không được trống");
        } else {
            if (request.getTen().length() < 1 || request.getTen().length() > 255) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên phải từ 1 -> 255 ký tự");
            } else if (!checkName(request.getTen())) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên không được chứa ký tự đặc biệt.");
            } else if (adDanhMucRepository.existsDanhMucByTenNotId(request.getTen(), id) != null) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên đã tồn tại, xin vui lòng nhập lại.");
            }
        }

        Optional<DanhMuc> DanhMuc = adDanhMucRepository.findById(id)
                .map(DanhMuc1 -> {
                    DanhMuc1.setTen(request.getTen());
                    return adDanhMucRepository.save(DanhMuc1);
                });
        return DanhMuc
                .map(DanhMuc1 -> new ResponseObject<>(DanhMuc1, HttpStatus.OK,
                        "Cập nhật danh mục thành công."))
                .orElseGet(() -> new ResponseObject<>(null, HttpStatus.NOT_FOUND,
                        "danh mục không tồn tại."));
    }

    @Override
    public ResponseObject<?> deleted(String id) {
        Optional<DanhMuc> DanhMuc = adDanhMucRepository.findById(id)
                .map(DanhMuc1 -> {
                    DanhMuc1.setDeleted(true);
                    return adDanhMucRepository.save(DanhMuc1);
                });
        return DanhMuc
                .map(DanhMuc1 -> new ResponseObject<>(null, HttpStatus.OK,
                        "Xóa danh mục thành công."))
                .orElseGet(() -> new ResponseObject<>(null, HttpStatus.NOT_FOUND,
                        "danh mục không tồn tại."));
    }

    public boolean checkName(String name) {
        String regex = "^[a-zA-Z0-9àáãạảăắằẳẵặâấầẩẫậèéẹẻẽêềếểễệđìíĩỉịòóõọỏôốồổỗộơớờởỡợùúũụủư" +
                       "ứừửữựỳỵỷỹýÀÁÃẠẢĂẮẰẲẴẶÂẤẦẨẪẬÈÉẸẺẼÊỀẾỂỄỆĐÌÍĨỈỊÒÓÕỌỎÔỐỒỔỖỘƠỚỜỞỠỢÙÚŨỤỦƯỨỪỬỮỰỲỴỶỸÝ\\s]+$";
        return name != null && name.matches(regex);
    }
}
