package com.shop.server.core.admin.mau_sac.service.impl;

import com.shop.server.core.admin.mau_sac.model.request.AdCreateUpdateMauSacRequest;
import com.shop.server.core.admin.mau_sac.repository.AdMauSacRepository;
import com.shop.server.core.admin.mau_sac.service.AdMauSacService;
import com.shop.server.core.common.base.ResponseObject;
import com.shop.server.entities.main.MauSac;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class AdMauSacServiceImpl implements AdMauSacService {

    private final AdMauSacRepository adMauSacRepository;

    @Override
    public ResponseObject<?> getListMauSac() {
        return new ResponseObject<>(adMauSacRepository.getListMauSac(),
                HttpStatus.OK,
                "Lấy dữ liệu thành công."
        );
    }

    @Override
    public ResponseObject<?> createMauSac(AdCreateUpdateMauSacRequest request) {
        if (request.getTen() == null || request.getTen().isBlank()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên không được trống");
        } else {
            if (request.getTen().length() < 1 || request.getTen().length() > 255) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên phải từ 1 -> 255 ký tự");
            } else if (!checkName(request.getTen())) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên không được chứa ký tự đặc biệt.");
            } else if (adMauSacRepository.existsMauSacByTen(request.getTen())) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên đã tồn tại, xin vui lòng nhập lại.");
            }
        }

        MauSac mauSac = new MauSac();
        Random random = new Random();
        String code;
        do {
            int number = random.nextInt(1000);
            code = String.format("MS%04d", number);
        } while (adMauSacRepository.existsMauSacByMaMauSac(code));
        mauSac.setMaMauSac(code);
        mauSac.setTen(request.getTen());
        mauSac.setDeleted(false);
        MauSac mauSac1 = adMauSacRepository.save(mauSac);
        return new ResponseObject<>(mauSac1, HttpStatus.CREATED, "Tạo mau sac thành công.");
    }

    public boolean checkName(String name) {
        String regex = "^[a-zA-Z0-9àáãạảăắằẳẵặâấầẩẫậèéẹẻẽêềếểễệđìíĩỉịòóõọỏôốồổỗộơớờởỡợùúũụủư" +
                       "ứừửữựỳỵỷỹýÀÁÃẠẢĂẮẰẲẴẶÂẤẦẨẪẬÈÉẸẺẼÊỀẾỂỄỆĐÌÍĨỈỊÒÓÕỌỎÔỐỒỔỖỘƠỚỜỞỠỢÙÚŨỤỦƯỨỪỬỮỰỲỴỶỸÝ\\s]+$";
        return name != null && name.matches(regex);
    }
}
