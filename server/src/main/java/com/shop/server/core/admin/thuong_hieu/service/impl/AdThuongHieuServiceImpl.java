package com.shop.server.core.admin.thuong_hieu.service.impl;

import com.shop.server.core.admin.thuong_hieu.model.request.AdCreateUpdateThuongHieuRequest;
import com.shop.server.core.admin.thuong_hieu.model.request.AdFindThuongHieuRequest;
import com.shop.server.core.admin.thuong_hieu.repository.AdThuongHieuRepository;
import com.shop.server.core.admin.thuong_hieu.service.AdThuongHieuService;
import com.shop.server.core.common.base.PageableObject;
import com.shop.server.core.common.base.ResponseObject;
import com.shop.server.entities.main.ThuongHieu;
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
public class AdThuongHieuServiceImpl implements AdThuongHieuService {

    private final AdThuongHieuRepository adThuongHieuRepository;

    @Override
    public ResponseObject<?> getAllThuongHieus(AdFindThuongHieuRequest request) {
        Pageable pageable = Helper.createPageable(request);

        return new ResponseObject<>(
                PageableObject.of(adThuongHieuRepository.getAllThuongHieus(pageable, request)),
                HttpStatus.OK,
                "Lấy dữ liệu thành công."
        );
    }

    @Override
    public ResponseObject<?> getThuongHieu(String id) {

        return adThuongHieuRepository.findById(id)
                .map(ok -> new ResponseObject<>(ok, HttpStatus.OK, "Lấy thông tin thương hiệu thành công."))
                .orElseGet(() -> new ResponseObject<>(null, HttpStatus.NOT_FOUND,
                        "Id: " + id + " không tồn tại"));
    }

    @Override
    public ResponseObject<?> getListThuongHieu() {
        return new ResponseObject<>(adThuongHieuRepository.getListThuongHieu(),
                HttpStatus.OK,
                "Lấy dữ liệu thành công."
        );
    }

    @Override
    public ResponseObject<?> createThuongHieu(@Valid AdCreateUpdateThuongHieuRequest request) {

        if (request.getTen() == null || request.getTen().isBlank()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên không được trống");
        } else {
            if (request.getTen().length() < 1 || request.getTen().length() > 255) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên phải từ 1 -> 255 ký tự");
            } else if (!checkName(request.getTen())) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên không được chứa ký tự đặc biệt.");
            } else if (adThuongHieuRepository.existsThuongHieuByTen(request.getTen())) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên đã tồn tại, xin vui lòng nhập lại.");
            }
        }

        ThuongHieu ThuongHieu = new ThuongHieu();
        Random random = new Random();
        String code;
        do {
            int number = random.nextInt(1000);
            code = String.format("TH%04d", number);
        } while (adThuongHieuRepository.existsThuongHieuByMaThuongHieu(code));
        ThuongHieu.setMaThuongHieu(code);
        ThuongHieu.setTen(request.getTen());
        ThuongHieu.setDeleted(false);
        ThuongHieu addedThuongHieu = adThuongHieuRepository.save(ThuongHieu);
        return new ResponseObject<>(addedThuongHieu, HttpStatus.CREATED, "Tạo thương hiệu thành công.");
    }

    @Override
    public ResponseObject<?> updateThuongHieu(String id, @Valid AdCreateUpdateThuongHieuRequest request) {

        if (request.getTen() == null || request.getTen().isBlank()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên không được trống");
        } else {
            if (request.getTen().length() < 1 || request.getTen().length() > 255) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên phải từ 1 -> 255 ký tự");
            } else if (!checkName(request.getTen())) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên không được chứa ký tự đặc biệt.");
            } else if (adThuongHieuRepository.existsThuongHieuByTenNotId(request.getTen(), id) != null) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên đã tồn tại, xin vui lòng nhập lại.");
            }
        }

        Optional<ThuongHieu> ThuongHieu = adThuongHieuRepository.findById(id)
                .map(ThuongHieu1 -> {
                    ThuongHieu1.setTen(request.getTen());
                    return adThuongHieuRepository.save(ThuongHieu1);
                });
        return ThuongHieu
                .map(ThuongHieu1 -> new ResponseObject<>(ThuongHieu1, HttpStatus.OK,
                        "Cập nhật thương hiệu thành công."))
                .orElseGet(() -> new ResponseObject<>(null, HttpStatus.NOT_FOUND,
                        "thương hiệu không tồn tại."));
    }

    @Override
    public ResponseObject<?> deleted(String id) {
        Optional<ThuongHieu> ThuongHieu = adThuongHieuRepository.findById(id)
                .map(ThuongHieu1 -> {
                    ThuongHieu1.setDeleted(true);
                    return adThuongHieuRepository.save(ThuongHieu1);
                });
        return ThuongHieu
                .map(ThuongHieu1 -> new ResponseObject<>(null, HttpStatus.OK,
                        "Xóa thương hiệu thành công."))
                .orElseGet(() -> new ResponseObject<>(null, HttpStatus.NOT_FOUND,
                        "thương hiệu không tồn tại."));
    }

    public boolean checkName(String name) {
        String regex = "^[a-zA-Z0-9àáãạảăắằẳẵặâấầẩẫậèéẹẻẽêềếểễệđìíĩỉịòóõọỏôốồổỗộơớờởỡợùúũụủư" +
                       "ứừửữựỳỵỷỹýÀÁÃẠẢĂẮẰẲẴẶÂẤẦẨẪẬÈÉẸẺẼÊỀẾỂỄỆĐÌÍĨỈỊÒÓÕỌỎÔỐỒỔỖỘƠỚỜỞỠỢÙÚŨỤỦƯỨỪỬỮỰỲỴỶỸÝ\\s]+$";
        return name != null && name.matches(regex);
    }
}
