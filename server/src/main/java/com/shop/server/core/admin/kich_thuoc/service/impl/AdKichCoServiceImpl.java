package com.shop.server.core.admin.kich_thuoc.service.impl;

import com.shop.server.core.admin.kich_thuoc.model.request.AdCreateUpdateKichCoRequest;
import com.shop.server.core.admin.kich_thuoc.repository.AdKichCoRepository;
import com.shop.server.core.admin.kich_thuoc.service.AdKichCoService;
import com.shop.server.core.common.base.ResponseObject;
import com.shop.server.entities.main.KichCo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class AdKichCoServiceImpl implements AdKichCoService {

    private final AdKichCoRepository adKichCoRepository;

    @Override
    public ResponseObject<?> getListKichCo() {
        return new ResponseObject<>(adKichCoRepository.getListKichCo(),
                HttpStatus.OK,
                "Lấy dữ liệu thành công."
        );
    }

    @Override
    public ResponseObject<?> createKichCo(AdCreateUpdateKichCoRequest request) {
        if (request.getTen() == null || request.getTen().isBlank()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên không được trống");
        } else {
            if (request.getTen().length() < 1 || request.getTen().length() > 255) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên phải từ 1 -> 255 ký tự");
            } else if (!checkName(request.getTen())) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên không được chứa ký tự đặc biệt.");
            } else if (adKichCoRepository.existsKichCoByTen(request.getTen())) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên đã tồn tại, xin vui lòng nhập lại.");
            }
        }

        KichCo kichCo = new KichCo();
        Random random = new Random();
        String code;
        do {
            int number = random.nextInt(1000);
            code = String.format("KC%04d", number);
        } while (adKichCoRepository.existsKichCoByMaKichCo(code));
        kichCo.setMaKichCo(code);
        kichCo.setTen(request.getTen());
        kichCo.setDeleted(false);
        kichCo.setCanNangMin(request.getCanNangMin());
        kichCo.setCanNangMax(request.getCanNangMax());
        kichCo.setChieuCaoMin(request.getChieuCaoMin());
        kichCo.setChieuCaoMax(request.getChieuCaoMax());
        KichCo kichCo1 = adKichCoRepository.save(kichCo);
        return new ResponseObject<>(kichCo1, HttpStatus.CREATED, "Tạo kích cỡ thành công.");
    }

    public boolean checkName(String name) {
        String regex = "^[a-zA-Z0-9àáãạảăắằẳẵặâấầẩẫậèéẹẻẽêềếểễệđìíĩỉịòóõọỏôốồổỗộơớờởỡợùúũụủư" +
                       "ứừửữựỳỵỷỹýÀÁÃẠẢĂẮẰẲẴẶÂẤẦẨẪẬÈÉẸẺẼÊỀẾỂỄỆĐÌÍĨỈỊÒÓÕỌỎÔỐỒỔỖỘƠỚỜỞỠỢÙÚŨỤỦƯỨỪỬỮỰỲỴỶỸÝ\\s]+$";
        return name != null && name.matches(regex);
    }
}
