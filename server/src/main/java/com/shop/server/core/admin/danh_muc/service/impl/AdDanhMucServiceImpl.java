package com.shop.server.core.admin.danh_muc.service.impl;

import com.shop.server.core.admin.danh_muc.model.request.AdCreateUpdateDanhMucRequest;
import com.shop.server.core.admin.danh_muc.repository.AdDanhMucRepository;
import com.shop.server.core.admin.danh_muc.service.AdDanhMucService;
import com.shop.server.core.common.base.ResponseObject;
import com.shop.server.entities.main.DanhMuc;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class AdDanhMucServiceImpl implements AdDanhMucService {

    private final AdDanhMucRepository adDanhMucRepository;

    @Override
    public ResponseObject<?> getListDanhMuc() {
        return new ResponseObject<>(adDanhMucRepository.getListDanhMuc(),
                HttpStatus.OK,
                "Lấy dữ liệu thành công."
        );
    }

    @Override
    public ResponseObject<?> createDanhMuc(AdCreateUpdateDanhMucRequest request) {
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

        DanhMuc danhMuc = new DanhMuc();
        Random random = new Random();
        String code;
        do {
            int number = random.nextInt(1000);
            code = String.format("CA%04d", number);
        } while (adDanhMucRepository.existsDanhMucByMaDanhMuc(code));
        danhMuc.setMaDanhMuc(code);
        danhMuc.setTen(request.getTen());
        danhMuc.setDeleted(false);
        DanhMuc danhMuc1 = adDanhMucRepository.save(danhMuc);
        return new ResponseObject<>(danhMuc1, HttpStatus.CREATED, "Tạo danh mục thành công.");
    }

    public boolean checkName(String name) {
        String regex = "^[a-zA-Z0-9àáãạảăắằẳẵặâấầẩẫậèéẹẻẽêềếểễệđìíĩỉịòóõọỏôốồổỗộơớờởỡợùúũụủư" +
                       "ứừửữựỳỵỷỹýÀÁÃẠẢĂẮẰẲẴẶÂẤẦẨẪẬÈÉẸẺẼÊỀẾỂỄỆĐÌÍĨỈỊÒÓÕỌỎÔỐỒỔỖỘƠỚỜỞỠỢÙÚŨỤỦƯỨỪỬỮỰỲỴỶỸÝ\\s]+$";
        return name != null && name.matches(regex);
    }
}
