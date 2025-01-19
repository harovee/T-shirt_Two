package com.shop.server.core.admin.tay_ao.service.impl;

import com.shop.server.core.admin.tay_ao.model.request.AdCreateUpdateTayAoRequest;
import com.shop.server.core.admin.tay_ao.model.request.AdFindTayAoRequest;
import com.shop.server.core.admin.tay_ao.repository.AdTayAoRepository;
import com.shop.server.core.admin.tay_ao.service.AdTayAoService;
import com.shop.server.core.common.base.PageableObject;
import com.shop.server.core.common.base.ResponseObject;
import com.shop.server.entities.main.TayAo;
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
public class AdTayAoServiceImpl implements AdTayAoService {

    private final AdTayAoRepository adTayAoRepository;

    @Override
    public ResponseObject<?> getAllTayAos(AdFindTayAoRequest request) {
        Pageable pageable = Helper.createPageable(request);

        return new ResponseObject<>(
                PageableObject.of(adTayAoRepository.getAllTayAos(pageable, request)),
                HttpStatus.OK,
                "Lấy dữ liệu thành công."
        );
    }

    @Override
    public ResponseObject<?> getTayAo(String id) {

        return adTayAoRepository.findById(id)
                .map(ok -> new ResponseObject<>(ok, HttpStatus.OK, "Lấy thông tin tay áo thành công."))
                .orElseGet(() -> new ResponseObject<>(null, HttpStatus.NOT_FOUND,
                        "Id: " + id + " không tồn tại"));
    }

    @Override
    public ResponseObject<?> getListTayAo() {
        return new ResponseObject<>(adTayAoRepository.getListTayAo(),
                HttpStatus.OK,
                "Lấy dữ liệu thành công."
        );
    }

    @Override
    public ResponseObject<?> createTayAo(@Valid AdCreateUpdateTayAoRequest request) {

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

        TayAo TayAo = new TayAo();
        Random random = new Random();
        String code;
        do {
            int number = random.nextInt(1000);
            code = String.format("TA%04d", number);
        } while (adTayAoRepository.existsTayAoByMaTayAo(code));
        TayAo.setMaTayAo(code);
        TayAo.setTen(request.getTen());
        TayAo.setDeleted(false);
        TayAo addedTayAo = adTayAoRepository.save(TayAo);
        return new ResponseObject<>(addedTayAo, HttpStatus.CREATED, "Tạo tay áo thành công.");
    }

    @Override
    public ResponseObject<?> updateTayAo(String id, @Valid AdCreateUpdateTayAoRequest request) {

        if (request.getTen() == null || request.getTen().isBlank()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên không được trống");
        } else {
            if (request.getTen().length() < 1 || request.getTen().length() > 255) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên phải từ 1 -> 255 ký tự");
            } else if (!checkName(request.getTen())) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên không được chứa ký tự đặc biệt.");
            } else if (adTayAoRepository.existsTayAoByTenNotId(request.getTen(), id) != null) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên đã tồn tại, xin vui lòng nhập lại.");
            }
        }

        Optional<TayAo> TayAo = adTayAoRepository.findById(id)
                .map(TayAo1 -> {
                    TayAo1.setTen(request.getTen());
                    return adTayAoRepository.save(TayAo1);
                });
        return TayAo
                .map(TayAo1 -> new ResponseObject<>(TayAo1, HttpStatus.OK,
                        "Cập nhật tay áo thành công."))
                .orElseGet(() -> new ResponseObject<>(null, HttpStatus.NOT_FOUND,
                        "tay áo không tồn tại."));
    }

    @Override
    public ResponseObject<?> deleted(String id) {
        Optional<TayAo> TayAo = adTayAoRepository.findById(id)
                .map(TayAo1 -> {
                    TayAo1.setDeleted(true);
                    return adTayAoRepository.save(TayAo1);
                });
        return TayAo
                .map(TayAo1 -> new ResponseObject<>(null, HttpStatus.OK,
                        "Xóa tay áo thành công."))
                .orElseGet(() -> new ResponseObject<>(null, HttpStatus.NOT_FOUND,
                        "tay áo không tồn tại."));
    }

    public boolean checkName(String name) {
        String regex = "^[a-zA-Z0-9àáãạảăắằẳẵặâấầẩẫậèéẹẻẽêềếểễệđìíĩỉịòóõọỏôốồổỗộơớờởỡợùúũụủư" +
                       "ứừửữựỳỵỷỹýÀÁÃẠẢĂẮẰẲẴẶÂẤẦẨẪẬÈÉẸẺẼÊỀẾỂỄỆĐÌÍĨỈỊÒÓÕỌỎÔỐỒỔỖỘƠỚỜỞỠỢÙÚŨỤỦƯỨỪỬỮỰỲỴỶỸÝ\\s]+$";
        return name != null && name.matches(regex);
    }
}
