package com.shop.server.core.admin.tay_ao.service.impl;

import com.shop.server.core.admin.tay_ao.model.request.AdCreateUpdateTayAoRequest;
import com.shop.server.core.admin.tay_ao.repository.AdTayAoRepository;
import com.shop.server.core.admin.tay_ao.service.AdTayAoService;
import com.shop.server.core.common.base.ResponseObject;
import com.shop.server.entities.main.TayAo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class AdTayAoServiceImpl implements AdTayAoService {

    private final AdTayAoRepository adTayAoRepository;

    @Override
    public ResponseObject<?> getListTayAo() {
        return new ResponseObject<>(adTayAoRepository.getListTayAo(),
                HttpStatus.OK,
                "Lấy dữ liệu thành công."
        );
    }

    @Override
    public ResponseObject<?> createTayAo(AdCreateUpdateTayAoRequest request) {
        if (request.getTen() == null || request.getTen().isBlank()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên không được trống");
        } else {
            if (request.getTen().length() < 1 || request.getTen().length() > 255) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên phải từ 1 -> 255 ký tự");
            } else if (!checkName(request.getTen())) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên không được chứa ký tự đặc biệt.");
            } else if (adTayAoRepository.existsTayAoByTen(request.getTen())) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên đã tồn tại, xin vui lòng nhập lại.");
            }
        }

        TayAo tayAo = new TayAo();
        Random random = new Random();
        String code;
        do {
            int number = random.nextInt(1000);
            code = String.format("TA%04d", number);
        } while (adTayAoRepository.existsTayAoByMaTayAo(code));
        tayAo.setMaTayAo(code);
        tayAo.setTen(request.getTen());
        tayAo.setDeleted(false);
        TayAo tayAo1 = adTayAoRepository.save(tayAo);
        return new ResponseObject<>(tayAo1, HttpStatus.CREATED, "Tạo tay áo thành công.");
    }

    public boolean checkName(String name) {
        String regex = "^[a-zA-Z0-9àáãạảăắằẳẵặâấầẩẫậèéẹẻẽêềếểễệđìíĩỉịòóõọỏôốồổỗộơớờởỡợùúũụủư" +
                       "ứừửữựỳỵỷỹýÀÁÃẠẢĂẮẰẲẴẶÂẤẦẨẪẬÈÉẸẺẼÊỀẾỂỄỆĐÌÍĨỈỊÒÓÕỌỎÔỐỒỔỖỘƠỚỜỞỠỢÙÚŨỤỦƯỨỪỬỮỰỲỴỶỸÝ\\s]+$";
        return name != null && name.matches(regex);
    }
}
