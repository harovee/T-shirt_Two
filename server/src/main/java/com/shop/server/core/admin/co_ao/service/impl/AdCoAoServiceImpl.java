package com.shop.server.core.admin.co_ao.service.impl;

import com.shop.server.core.admin.co_ao.model.request.AdCreateUpdateCoAoRequest;
import com.shop.server.core.admin.co_ao.model.request.AdFindCoAoRequest;
import com.shop.server.core.admin.co_ao.repository.AdCoAoRepository;
import com.shop.server.core.admin.co_ao.service.AdCoAoService;
import com.shop.server.core.common.base.PageableObject;
import com.shop.server.core.common.base.ResponseObject;
import com.shop.server.entities.main.CoAo;
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
public class AdCoAoServiceImpl implements AdCoAoService {

    private final AdCoAoRepository adCoAoRepository;

    @Override
    public ResponseObject<?> getAllCoAos(AdFindCoAoRequest request) {
        Pageable pageable = Helper.createPageable(request);

        return new ResponseObject<>(
                PageableObject.of(adCoAoRepository.getAllCoAos(pageable, request)),
                HttpStatus.OK,
                "Lấy dữ liệu thành công."
        );
    }

    @Override
    public ResponseObject<?> getCoAo(String id) {

        return adCoAoRepository.findById(id)
                .map(ok -> new ResponseObject<>(ok, HttpStatus.OK, "Lấy thông tin cổ áo thành công."))
                .orElseGet(() -> new ResponseObject<>(null, HttpStatus.NOT_FOUND,
                        "Id: " + id + " không tồn tại"));
    }

    @Override
    public ResponseObject<?> getListCoAo() {
        return new ResponseObject<>(adCoAoRepository.getListCoAo(),
                HttpStatus.OK,
                "Lấy dữ liệu thành công."
        );
    }

    @Override
    public ResponseObject<?> createCoAo(@Valid AdCreateUpdateCoAoRequest request) {

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

        CoAo CoAo = new CoAo();
        Random random = new Random();
        String code;
        do {
            int number = random.nextInt(1000);
            code = String.format("CA%04d", number);
        } while (adCoAoRepository.existsCoAoByMaCoAo(code));
        CoAo.setMaCoAo(code);
        CoAo.setTen(request.getTen());
        CoAo.setDeleted(false);
        CoAo addedCoAo = adCoAoRepository.save(CoAo);
        return new ResponseObject<>(addedCoAo, HttpStatus.CREATED, "Tạo cổ áo thành công.");
    }

    @Override
    public ResponseObject<?> updateCoAo(String id, @Valid AdCreateUpdateCoAoRequest request) {

        if (request.getTen() == null || request.getTen().isBlank()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên không được trống");
        } else {
            if (request.getTen().length() < 1 || request.getTen().length() > 255) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên phải từ 1 -> 255 ký tự");
            } else if (!checkName(request.getTen())) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên không được chứa ký tự đặc biệt.");
            } else if (adCoAoRepository.existsCoAoByTenNotId(request.getTen(), id) != null) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên đã tồn tại, xin vui lòng nhập lại.");
            }
        }

        Optional<CoAo> CoAo = adCoAoRepository.findById(id)
                .map(CoAo1 -> {
                    CoAo1.setTen(request.getTen());
                    return adCoAoRepository.save(CoAo1);
                });
        return CoAo
                .map(CoAo1 -> new ResponseObject<>(CoAo1, HttpStatus.OK,
                        "Cập nhật cổ áo thành công."))
                .orElseGet(() -> new ResponseObject<>(null, HttpStatus.NOT_FOUND,
                        "cổ áo không tồn tại."));
    }

    @Override
    public ResponseObject<?> deleted(String id) {
        Optional<CoAo> CoAo = adCoAoRepository.findById(id)
                .map(CoAo1 -> {
                    CoAo1.setDeleted(true);
                    return adCoAoRepository.save(CoAo1);
                });
        return CoAo
                .map(CoAo1 -> new ResponseObject<>(null, HttpStatus.OK,
                        "Xóa cổ áo thành công."))
                .orElseGet(() -> new ResponseObject<>(null, HttpStatus.NOT_FOUND,
                        "cổ áo không tồn tại."));
    }

    public boolean checkName(String name) {
        String regex = "^[a-zA-Z0-9àáãạảăắằẳẵặâấầẩẫậèéẹẻẽêềếểễệđìíĩỉịòóõọỏôốồổỗộơớờởỡợùúũụủư" +
                       "ứừửữựỳỵỷỹýÀÁÃẠẢĂẮẰẲẴẶÂẤẦẨẪẬÈÉẸẺẼÊỀẾỂỄỆĐÌÍĨỈỊÒÓÕỌỎÔỐỒỔỖỘƠỚỜỞỠỢÙÚŨỤỦƯỨỪỬỮỰỲỴỶỸÝ\\s]+$";
        return name != null && name.matches(regex);
    }
}
