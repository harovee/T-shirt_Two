package com.shop.server.core.admin.co_ao.service.impl;

import com.shop.server.core.admin.co_ao.model.request.AdCreateUpdateCoAoRequest;
import com.shop.server.core.admin.co_ao.repository.AdCoAoRepository;
import com.shop.server.core.admin.co_ao.service.AdCoAoService;
import com.shop.server.core.common.base.ResponseObject;
import com.shop.server.entities.main.CoAo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class AdCoAoServiceImpl implements AdCoAoService {

    private final AdCoAoRepository adCoAoRepository;

    @Override
    public ResponseObject<?> getListCoAo() {
        return new ResponseObject<>(adCoAoRepository.getListCoAo(),
                HttpStatus.OK,
                "Lấy dữ liệu thành công."
        );
    }

    @Override
    public ResponseObject<?> createCoAo(AdCreateUpdateCoAoRequest request) {
        if (request.getTen() == null || request.getTen().isBlank()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên không được trống");
        } else {
            if (request.getTen().length() < 1 || request.getTen().length() > 255) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên phải từ 1 -> 255 ký tự");
            } else if (!checkName(request.getTen())) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên không được chứa ký tự đặc biệt.");
            } else if (adCoAoRepository.existsCoAoByTen(request.getTen())) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên đã tồn tại, xin vui lòng nhập lại.");
            }
        }

        CoAo coAo = new CoAo();
        Random random = new Random();
        String code;
        do {
            int number = random.nextInt(1000);
            code = String.format("CA%04d", number);
        } while (adCoAoRepository.existsCoAoByMaCoAo(code));
        coAo.setMaCoAo(code);
        coAo.setTen(request.getTen());
        coAo.setDeleted(false);
        CoAo coAo1 = adCoAoRepository.save(coAo);
        return new ResponseObject<>(coAo1, HttpStatus.CREATED, "Tạo cổ áo thành công.");
    }

    public boolean checkName(String name) {
        String regex = "^[a-zA-Z0-9àáãạảăắằẳẵặâấầẩẫậèéẹẻẽêềếểễệđìíĩỉịòóõọỏôốồổỗộơớờởỡợùúũụủư" +
                       "ứừửữựỳỵỷỹýÀÁÃẠẢĂẮẰẲẴẶÂẤẦẨẪẬÈÉẸẺẼÊỀẾỂỄỆĐÌÍĨỈỊÒÓÕỌỎÔỐỒỔỖỘƠỚỜỞỠỢÙÚŨỤỦƯỨỪỬỮỰỲỴỶỸÝ\\s]+$";
        return name != null && name.matches(regex);
    }
}
