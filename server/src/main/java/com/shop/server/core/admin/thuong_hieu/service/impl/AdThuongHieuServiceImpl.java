package com.shop.server.core.admin.thuong_hieu.service.impl;

import com.shop.server.core.admin.thuong_hieu.model.request.AdCreateUpdateThuongHieuRequest;
import com.shop.server.core.admin.thuong_hieu.repository.AdThuongHieuRepository;
import com.shop.server.core.admin.thuong_hieu.service.AdThuongHieuService;
import com.shop.server.core.common.base.ResponseObject;
import com.shop.server.entities.main.ThuongHieu;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class AdThuongHieuServiceImpl implements AdThuongHieuService {

    private final AdThuongHieuRepository adThuongHieuRepository;

    @Override
    public ResponseObject<?> getListThuongHieu() {
        return new ResponseObject<>(adThuongHieuRepository.getListThuongHieu(),
                HttpStatus.OK,
                "Lấy dữ liệu thành công."
        );
    }

    @Override
    public ResponseObject<?> createThuongHieu(AdCreateUpdateThuongHieuRequest request) {
        if (request.getTen() == null || request.getTen().isBlank()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên không được trống");
        } else {
            if (request.getTen().length() < 1 || request.getTen().length() > 255) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên phải từ 1 -> 255 ký tự");
            } else if (!checkName(request.getTen())) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên không được chứa ký tự đặc biệt.");
            } else if (adThuongHieuRepository.existsThuongHieuByTen(request.getTen())) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên đã tồn tại, xin vui lòng nhập lại.");
            }
        }

        ThuongHieu thuongHieu = new ThuongHieu();
        Random random = new Random();
        String code;
        do {
            int number = random.nextInt(1000);
            code = String.format("TH%04d", number);
        } while (adThuongHieuRepository.existsThuongHieuByMaThuongHieu(code));
        thuongHieu.setMaThuongHieu(code);
        thuongHieu.setTen(request.getTen());
        thuongHieu.setDeleted(false);
        ThuongHieu thuongHieu1 = adThuongHieuRepository.save(thuongHieu);
        return new ResponseObject<>(thuongHieu1, HttpStatus.CREATED, "Tạo thương hiệu thành công.");
    }

    public boolean checkName(String name) {
        String regex = "^[a-zA-Z0-9àáãạảăắằẳẵặâấầẩẫậèéẹẻẽêềếểễệđìíĩỉịòóõọỏôốồổỗộơớờởỡợùúũụủư" +
                       "ứừửữựỳỵỷỹýÀÁÃẠẢĂẮẰẲẴẶÂẤẦẨẪẬÈÉẸẺẼÊỀẾỂỄỆĐÌÍĨỈỊÒÓÕỌỎÔỐỒỔỖỘƠỚỜỞỠỢÙÚŨỤỦƯỨỪỬỮỰỲỴỶỸÝ\\s]+$";
        return name != null && name.matches(regex);
    }
}
