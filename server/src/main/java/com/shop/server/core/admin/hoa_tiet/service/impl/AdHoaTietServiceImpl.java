package com.shop.server.core.admin.hoa_tiet.service.impl;

import com.shop.server.core.admin.hoa_tiet.model.request.AdCreateUpdateHoaTietRequest;
import com.shop.server.core.admin.hoa_tiet.repository.AdHoaTietRepository;
import com.shop.server.core.admin.hoa_tiet.service.AdHoaTietService;
import com.shop.server.core.common.base.ResponseObject;
import com.shop.server.entities.main.HoaTiet;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class AdHoaTietServiceImpl implements AdHoaTietService {

    private final AdHoaTietRepository adHoaTietRepository;

    @Override
    public ResponseObject<?> getListHoaTiet() {
        return new ResponseObject<>(adHoaTietRepository.getListHoaTiet(),
                HttpStatus.OK,
                "Lấy dữ liệu thành công."
        );
    }

    @Override
    public ResponseObject<?> createHoaTiet(AdCreateUpdateHoaTietRequest request) {
        if (request.getTen() == null || request.getTen().isBlank()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên không được trống");
        } else {
            if (request.getTen().length() < 1 || request.getTen().length() > 255) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên phải từ 1 -> 255 ký tự");
            } else if (!checkName(request.getTen())) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên không được chứa ký tự đặc biệt.");
            } else if (adHoaTietRepository.existsHoaTietByTen(request.getTen())) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên đã tồn tại, xin vui lòng nhập lại.");
            }
        }

        HoaTiet hoaTiet = new HoaTiet();
        Random random = new Random();
        String code;
        do {
            int number = random.nextInt(1000);
            code = String.format("HT%04d", number);
        } while (adHoaTietRepository.existsHoaTietByMaHoaTiet(code));
        hoaTiet.setMaHoaTiet(code);
        hoaTiet.setTen(request.getTen());
        hoaTiet.setDeleted(false);
        HoaTiet hoaTiet1 = adHoaTietRepository.save(hoaTiet);
        return new ResponseObject<>(hoaTiet1, HttpStatus.CREATED, "Tạo họa tiết thành công.");
    }

    public boolean checkName(String name) {
        String regex = "^[a-zA-Z0-9àáãạảăắằẳẵặâấầẩẫậèéẹẻẽêềếểễệđìíĩỉịòóõọỏôốồổỗộơớờởỡợùúũụủư" +
                       "ứừửữựỳỵỷỹýÀÁÃẠẢĂẮẰẲẴẶÂẤẦẨẪẬÈÉẸẺẼÊỀẾỂỄỆĐÌÍĨỈỊÒÓÕỌỎÔỐỒỔỖỘƠỚỜỞỠỢÙÚŨỤỦƯỨỪỬỮỰỲỴỶỸÝ\\s]+$";
        return name != null && name.matches(regex);
    }
}
