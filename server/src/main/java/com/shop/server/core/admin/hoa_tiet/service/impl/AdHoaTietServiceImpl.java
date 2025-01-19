package com.shop.server.core.admin.hoa_tiet.service.impl;

import com.shop.server.core.admin.hoa_tiet.model.request.AdCreateUpdateHoaTietRequest;
import com.shop.server.core.admin.hoa_tiet.model.request.AdFindHoaTietRequest;
import com.shop.server.core.admin.hoa_tiet.repository.AdHoaTietRepository;
import com.shop.server.core.admin.hoa_tiet.service.AdHoaTietService;
import com.shop.server.core.common.base.PageableObject;
import com.shop.server.core.common.base.ResponseObject;
import com.shop.server.entities.main.HoaTiet;
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
public class AdHoaTietServiceImpl implements AdHoaTietService {

    private final AdHoaTietRepository adHoaTietRepository;

    @Override
    public ResponseObject<?> getAllHoaTiets(AdFindHoaTietRequest request) {
        Pageable pageable = Helper.createPageable(request);

        return new ResponseObject<>(
                PageableObject.of(adHoaTietRepository.getAllHoaTiets(pageable, request)),
                HttpStatus.OK,
                "Lấy dữ liệu thành công."
        );
    }

    @Override
    public ResponseObject<?> getHoaTiet(String id) {

        return adHoaTietRepository.findById(id)
                .map(ok -> new ResponseObject<>(ok, HttpStatus.OK, "Lấy thông tin họa tiết thành công."))
                .orElseGet(() -> new ResponseObject<>(null, HttpStatus.NOT_FOUND,
                        "Id: " + id + " không tồn tại"));
    }

    @Override
    public ResponseObject<?> getListHoaTiet() {
        return new ResponseObject<>(adHoaTietRepository.getListHoaTiet(),
                HttpStatus.OK,
                "Lấy dữ liệu thành công."
        );
    }

    @Override
    public ResponseObject<?> createHoaTiet(@Valid AdCreateUpdateHoaTietRequest request) {

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

        HoaTiet HoaTiet = new HoaTiet();
        Random random = new Random();
        String code;
        do {
            int number = random.nextInt(1000);
            code = String.format("HT%04d", number);
        } while (adHoaTietRepository.existsHoaTietByMaHoaTiet(code));
        HoaTiet.setMaHoaTiet(code);
        HoaTiet.setTen(request.getTen());
        HoaTiet.setDeleted(false);
        HoaTiet addedHoaTiet = adHoaTietRepository.save(HoaTiet);
        return new ResponseObject<>(addedHoaTiet, HttpStatus.CREATED, "Tạo họa tiết thành công.");
    }

    @Override
    public ResponseObject<?> updateHoaTiet(String id, @Valid AdCreateUpdateHoaTietRequest request) {

        if (request.getTen() == null || request.getTen().isBlank()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên không được trống");
        } else {
            if (request.getTen().length() < 1 || request.getTen().length() > 255) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên phải từ 1 -> 255 ký tự");
            } else if (!checkName(request.getTen())) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên không được chứa ký tự đặc biệt.");
            } else if (adHoaTietRepository.existsHoaTietByTenNotId(request.getTen(), id) != null) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên đã tồn tại, xin vui lòng nhập lại.");
            }
        }

        Optional<HoaTiet> HoaTiet = adHoaTietRepository.findById(id)
                .map(HoaTiet1 -> {
                    HoaTiet1.setTen(request.getTen());
                    return adHoaTietRepository.save(HoaTiet1);
                });
        return HoaTiet
                .map(HoaTiet1 -> new ResponseObject<>(HoaTiet1, HttpStatus.OK,
                        "Cập nhật họa tiết thành công."))
                .orElseGet(() -> new ResponseObject<>(null, HttpStatus.NOT_FOUND,
                        "họa tiết không tồn tại."));
    }

    @Override
    public ResponseObject<?> deleted(String id) {
        Optional<HoaTiet> HoaTiet = adHoaTietRepository.findById(id)
                .map(HoaTiet1 -> {
                    HoaTiet1.setDeleted(true);
                    return adHoaTietRepository.save(HoaTiet1);
                });
        return HoaTiet
                .map(HoaTiet1 -> new ResponseObject<>(null, HttpStatus.OK,
                        "Xóa họa tiết thành công."))
                .orElseGet(() -> new ResponseObject<>(null, HttpStatus.NOT_FOUND,
                        "họa tiết không tồn tại."));
    }

    public boolean checkName(String name) {
        String regex = "^[a-zA-Z0-9àáãạảăắằẳẵặâấầẩẫậèéẹẻẽêềếểễệđìíĩỉịòóõọỏôốồổỗộơớờởỡợùúũụủư" +
                       "ứừửữựỳỵỷỹýÀÁÃẠẢĂẮẰẲẴẶÂẤẦẨẪẬÈÉẸẺẼÊỀẾỂỄỆĐÌÍĨỈỊÒÓÕỌỎÔỐỒỔỖỘƠỚỜỞỠỢÙÚŨỤỦƯỨỪỬỮỰỲỴỶỸÝ\\s]+$";
        return name != null && name.matches(regex);
    }
}
