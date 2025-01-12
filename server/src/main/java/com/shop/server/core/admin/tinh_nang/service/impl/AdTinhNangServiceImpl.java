package com.shop.server.core.admin.tinh_nang.service.impl;

import com.shop.server.core.admin.tinh_nang.model.request.AdCreateUpdateTinhNangRequest;
import com.shop.server.core.admin.tinh_nang.repository.AdTinhNangRepository;
import com.shop.server.core.admin.tinh_nang.service.AdTinhNangService;
import com.shop.server.core.common.base.ResponseObject;
import com.shop.server.entities.main.TinhNang;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class AdTinhNangServiceImpl implements AdTinhNangService {

    private final AdTinhNangRepository adTinhNangRepository;

    @Override
    public ResponseObject<?> getListTinhNang() {
        return new ResponseObject<>(adTinhNangRepository.getListTinhNang(),
                HttpStatus.OK,
                "Lấy dữ liệu thành công."
        );
    }

    @Override
    public ResponseObject<?> createTinhNang(AdCreateUpdateTinhNangRequest request) {
        if (request.getTen() == null || request.getTen().isBlank()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên không được trống");
        } else {
            if (request.getTen().length() < 1 || request.getTen().length() > 255) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên phải từ 1 -> 255 ký tự");
            } else if (!checkName(request.getTen())) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên không được chứa ký tự đặc biệt.");
            } else if (adTinhNangRepository.existsTinhNangByTen(request.getTen())) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên đã tồn tại, xin vui lòng nhập lại.");
            }
        }

        TinhNang tinhNang = new TinhNang();
        Random random = new Random();
        String code;
        do {
            int number = random.nextInt(1000);
            code = String.format("TN%04d", number);
        } while (adTinhNangRepository.existsTinhNangByMaTinhNang(code));
        tinhNang.setMaTinhNang(code);
        tinhNang.setTen(request.getTen());
        tinhNang.setDeleted(false);
        TinhNang tinhNang1 = adTinhNangRepository.save(tinhNang);
        return new ResponseObject<>(tinhNang1, HttpStatus.CREATED, "Tạo cổ áo thành công.");
    }

    public boolean checkName(String name) {
        String regex = "^[a-zA-Z0-9àáãạảăắằẳẵặâấầẩẫậèéẹẻẽêềếểễệđìíĩỉịòóõọỏôốồổỗộơớờởỡợùúũụủư" +
                       "ứừửữựỳỵỷỹýÀÁÃẠẢĂẮẰẲẴẶÂẤẦẨẪẬÈÉẸẺẼÊỀẾỂỄỆĐÌÍĨỈỊÒÓÕỌỎÔỐỒỔỖỘƠỚỜỞỠỢÙÚŨỤỦƯỨỪỬỮỰỲỴỶỸÝ\\s]+$";
        return name != null && name.matches(regex);
    }
}
