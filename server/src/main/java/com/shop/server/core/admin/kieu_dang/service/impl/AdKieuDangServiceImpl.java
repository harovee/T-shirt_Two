package com.shop.server.core.admin.kieu_dang.service.impl;

import com.shop.server.core.admin.kieu_dang.model.request.AdCreateUpdateKieuDangRequest;
import com.shop.server.core.admin.kieu_dang.repository.AdKieuDangRepository;
import com.shop.server.core.admin.kieu_dang.service.AdKieuDangService;
import com.shop.server.core.common.base.ResponseObject;
import com.shop.server.entities.main.KieuDang;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class AdKieuDangServiceImpl implements AdKieuDangService {

    private final AdKieuDangRepository adKieuDangRepository;

    @Override
    public ResponseObject<?> getListKieuDang() {
        return new ResponseObject<>(adKieuDangRepository.getListKieuDang(),
                HttpStatus.OK,
                "Lấy dữ liệu thành công."
        );
    }

    @Override
    public ResponseObject<?> createKieuDang(AdCreateUpdateKieuDangRequest request) {
        if (request.getTen() == null || request.getTen().isBlank()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên không được trống");
        } else {
            if (request.getTen().length() < 1 || request.getTen().length() > 255) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên phải từ 1 -> 255 ký tự");
            } else if (!checkName(request.getTen())) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên không được chứa ký tự đặc biệt.");
            } else if (adKieuDangRepository.existsKieuDangByTen(request.getTen())) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên đã tồn tại, xin vui lòng nhập lại.");
            }
        }

        KieuDang kieuDang = new KieuDang();
        Random random = new Random();
        String code;
        do {
            int number = random.nextInt(1000);
            code = String.format("KD%04d", number);
        } while (adKieuDangRepository.existsKieuDangByMaKieuDang(code));
        kieuDang.setMaKieuDang(code);
        kieuDang.setTen(request.getTen());
        kieuDang.setDeleted(false);
        KieuDang kieuDang1 = adKieuDangRepository.save(kieuDang);
        return new ResponseObject<>(kieuDang1, HttpStatus.CREATED, "Tạo kiểu dáng thành công.");
    }

    public boolean checkName(String name) {
        String regex = "^[a-zA-Z0-9àáãạảăắằẳẵặâấầẩẫậèéẹẻẽêềếểễệđìíĩỉịòóõọỏôốồổỗộơớờởỡợùúũụủư" +
                       "ứừửữựỳỵỷỹýÀÁÃẠẢĂẮẰẲẴẶÂẤẦẨẪẬÈÉẸẺẼÊỀẾỂỄỆĐÌÍĨỈỊÒÓÕỌỎÔỐỒỔỖỘƠỚỜỞỠỢÙÚŨỤỦƯỨỪỬỮỰỲỴỶỸÝ\\s]+$";
        return name != null && name.matches(regex);
    }
}
