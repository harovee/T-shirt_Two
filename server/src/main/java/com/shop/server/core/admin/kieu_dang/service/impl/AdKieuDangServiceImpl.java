package com.shop.server.core.admin.kieu_dang.service.impl;

import com.shop.server.core.admin.kieu_dang.model.request.AdCreateUpdateKieuDangRequest;
import com.shop.server.core.admin.kieu_dang.model.request.AdFindKieuDangRequest;
import com.shop.server.core.admin.kieu_dang.repository.AdKieuDangRepository;
import com.shop.server.core.admin.kieu_dang.service.AdKieuDangService;
import com.shop.server.core.common.base.PageableObject;
import com.shop.server.core.common.base.ResponseObject;
import com.shop.server.entities.main.KieuDang;
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
public class AdKieuDangServiceImpl implements AdKieuDangService {

    private final AdKieuDangRepository adKieuDangRepository;

    @Override
    public ResponseObject<?> getAllKieuDangs(AdFindKieuDangRequest request) {
        Pageable pageable = Helper.createPageable(request);

        return new ResponseObject<>(
                PageableObject.of(adKieuDangRepository.getAllKieuDangs(pageable, request)),
                HttpStatus.OK,
                "Lấy dữ liệu thành công."
        );
    }

    @Override
    public ResponseObject<?> getKieuDang(String id) {

        return adKieuDangRepository.findById(id)
                .map(ok -> new ResponseObject<>(ok, HttpStatus.OK, "Lấy thông tin kiểu dáng thành công."))
                .orElseGet(() -> new ResponseObject<>(null, HttpStatus.NOT_FOUND,
                        "Id: " + id + " không tồn tại"));
    }

    @Override
    public ResponseObject<?> getListKieuDang() {
        return new ResponseObject<>(adKieuDangRepository.getListKieuDang(),
                HttpStatus.OK,
                "Lấy dữ liệu thành công."
        );
    }

    @Override
    public ResponseObject<?> createKieuDang(@Valid AdCreateUpdateKieuDangRequest request) {

        if (request.getTen() == null || request.getTen().isBlank()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên không được trống");
        } else {
            if (request.getTen().length() < 1 || request.getTen().length() > 255) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên phải từ 1 -> 255 ký tự");
            } else if (!checkName(request.getTen())) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên không được chứa ký tự đặc biệt.");
            } else if (adKieuDangRepository.existsKieuDangByTen(request.getTen())) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên đã tồn tại, xin vui lòng nhập lại.");
            }
        }

        KieuDang KieuDang = new KieuDang();
        Random random = new Random();
        String code;
        do {
            int number = random.nextInt(1000);
            code = String.format("KD%04d", number);
        } while (adKieuDangRepository.existsKieuDangByMaKieuDang(code));
        KieuDang.setMaKieuDang(code);
        KieuDang.setTen(request.getTen());
        KieuDang.setDeleted(false);
        KieuDang addedKieuDang = adKieuDangRepository.save(KieuDang);
        return new ResponseObject<>(addedKieuDang, HttpStatus.CREATED, "Tạo kiểu dáng thành công.");
    }

    @Override
    public ResponseObject<?> updateKieuDang(String id, @Valid AdCreateUpdateKieuDangRequest request) {

        if (request.getTen() == null || request.getTen().isBlank()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên không được trống");
        } else {
            if (request.getTen().length() < 1 || request.getTen().length() > 255) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên phải từ 1 -> 255 ký tự");
            } else if (!checkName(request.getTen())) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên không được chứa ký tự đặc biệt.");
            } else if (adKieuDangRepository.existsKieuDangByTenNotId(request.getTen(), id) != null) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên đã tồn tại, xin vui lòng nhập lại.");
            }
        }

        Optional<KieuDang> KieuDang = adKieuDangRepository.findById(id)
                .map(KieuDang1 -> {
                    KieuDang1.setTen(request.getTen());
                    return adKieuDangRepository.save(KieuDang1);
                });
        return KieuDang
                .map(KieuDang1 -> new ResponseObject<>(KieuDang1, HttpStatus.OK,
                        "Cập nhật kiểu dáng thành công."))
                .orElseGet(() -> new ResponseObject<>(null, HttpStatus.NOT_FOUND,
                        "kiểu dáng không tồn tại."));
    }

    @Override
    public ResponseObject<?> deleted(String id) {
        Optional<KieuDang> KieuDang = adKieuDangRepository.findById(id)
                .map(KieuDang1 -> {
                    KieuDang1.setDeleted(true);
                    return adKieuDangRepository.save(KieuDang1);
                });
        return KieuDang
                .map(KieuDang1 -> new ResponseObject<>(null, HttpStatus.OK,
                        "Xóa kiểu dáng thành công."))
                .orElseGet(() -> new ResponseObject<>(null, HttpStatus.NOT_FOUND,
                        "kiểu dáng không tồn tại."));
    }

    public boolean checkName(String name) {
        String regex = "^[a-zA-Z0-9àáãạảăắằẳẵặâấầẩẫậèéẹẻẽêềếểễệđìíĩỉịòóõọỏôốồổỗộơớờởỡợùúũụủư" +
                       "ứừửữựỳỵỷỹýÀÁÃẠẢĂẮẰẲẴẶÂẤẦẨẪẬÈÉẸẺẼÊỀẾỂỄỆĐÌÍĨỈỊÒÓÕỌỎÔỐỒỔỖỘƠỚỜỞỠỢÙÚŨỤỦƯỨỪỬỮỰỲỴỶỸÝ\\s]+$";
        return name != null && name.matches(regex);
    }
}
