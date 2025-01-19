package com.shop.server.core.admin.mau_sac.service.impl;

import com.shop.server.core.admin.mau_sac.model.request.AdCreateUpdateMauSacRequest;
import com.shop.server.core.admin.mau_sac.model.request.AdFindMauSacRequest;
import com.shop.server.core.admin.mau_sac.repository.AdMauSacRepository;
import com.shop.server.core.admin.mau_sac.service.AdMauSacService;
import com.shop.server.core.common.base.PageableObject;
import com.shop.server.core.common.base.ResponseObject;
import com.shop.server.entities.main.MauSac;
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
public class AdMauSacServiceImpl implements AdMauSacService {

    private final AdMauSacRepository adMauSacRepository;

    @Override
    public ResponseObject<?> getAllMauSacs(AdFindMauSacRequest request) {
        Pageable pageable = Helper.createPageable(request);

        return new ResponseObject<>(
                PageableObject.of(adMauSacRepository.getAllMauSacs(pageable, request)),
                HttpStatus.OK,
                "Lấy dữ liệu thành công."
        );
    }

    @Override
    public ResponseObject<?> getMauSac(String id) {

        return adMauSacRepository.findById(id)
                .map(ok -> new ResponseObject<>(ok, HttpStatus.OK, "Lấy thông tin màu sắc thành công."))
                .orElseGet(() -> new ResponseObject<>(null, HttpStatus.NOT_FOUND,
                        "Id: " + id + " không tồn tại"));
    }

    @Override
    public ResponseObject<?> getListMauSac() {
        return new ResponseObject<>(adMauSacRepository.getListMauSac(),
                HttpStatus.OK,
                "Lấy dữ liệu thành công."
        );
    }

    @Override
    public ResponseObject<?> createMauSac(@Valid AdCreateUpdateMauSacRequest request) {

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

        MauSac MauSac = new MauSac();
        Random random = new Random();
        String code;
        do {
            int number = random.nextInt(1000);
            code = String.format("MS%04d", number);
        } while (adMauSacRepository.existsMauSacByMaMauSac(code));
        MauSac.setMaMauSac(code);
        MauSac.setTen(request.getTen());
        MauSac.setDeleted(false);
        MauSac addedMauSac = adMauSacRepository.save(MauSac);
        return new ResponseObject<>(addedMauSac, HttpStatus.CREATED, "Tạo màu sắc thành công.");
    }

    @Override
    public ResponseObject<?> updateMauSac(String id, @Valid AdCreateUpdateMauSacRequest request) {

        if (request.getTen() == null || request.getTen().isBlank()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên không được trống");
        } else {
            if (request.getTen().length() < 1 || request.getTen().length() > 255) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên phải từ 1 -> 255 ký tự");
            } else if (!checkName(request.getTen())) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên không được chứa ký tự đặc biệt.");
            } else if (adMauSacRepository.existsMauSacByTenNotId(request.getTen(), id) != null) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên đã tồn tại, xin vui lòng nhập lại.");
            }
        }

        Optional<MauSac> MauSac = adMauSacRepository.findById(id)
                .map(MauSac1 -> {
                    MauSac1.setTen(request.getTen());
                    return adMauSacRepository.save(MauSac1);
                });
        return MauSac
                .map(MauSac1 -> new ResponseObject<>(MauSac1, HttpStatus.OK,
                        "Cập nhật màu sắc thành công."))
                .orElseGet(() -> new ResponseObject<>(null, HttpStatus.NOT_FOUND,
                        "màu sắc không tồn tại."));
    }

    @Override
    public ResponseObject<?> deleted(String id) {
        Optional<MauSac> MauSac = adMauSacRepository.findById(id)
                .map(MauSac1 -> {
                    MauSac1.setDeleted(true);
                    return adMauSacRepository.save(MauSac1);
                });
        return MauSac
                .map(MauSac1 -> new ResponseObject<>(null, HttpStatus.OK,
                        "Xóa màu sắc thành công."))
                .orElseGet(() -> new ResponseObject<>(null, HttpStatus.NOT_FOUND,
                        "màu sắc không tồn tại."));
    }

    public boolean checkName(String name) {
        String regex = "^[a-zA-Z0-9àáãạảăắằẳẵặâấầẩẫậèéẹẻẽêềếểễệđìíĩỉịòóõọỏôốồổỗộơớờởỡợùúũụủư" +
                       "ứừửữựỳỵỷỹýÀÁÃẠẢĂẮẰẲẴẶÂẤẦẨẪẬÈÉẸẺẼÊỀẾỂỄỆĐÌÍĨỈỊÒÓÕỌỎÔỐỒỔỖỘƠỚỜỞỠỢÙÚŨỤỦƯỨỪỬỮỰỲỴỶỸÝ\\s]+$";
        return name != null && name.matches(regex);
    }
}
