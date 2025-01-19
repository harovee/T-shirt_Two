package com.shop.server.core.admin.tinh_nang.service.impl;

import com.shop.server.core.admin.tinh_nang.model.request.AdCreateUpdateTinhNangRequest;
import com.shop.server.core.admin.tinh_nang.model.request.AdFindTinhNangRequest;
import com.shop.server.core.admin.tinh_nang.repository.AdTinhNangRepository;
import com.shop.server.core.admin.tinh_nang.service.AdTinhNangService;
import com.shop.server.core.common.base.PageableObject;
import com.shop.server.core.common.base.ResponseObject;
import com.shop.server.entities.main.TinhNang;
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
public class AdTinhNangServiceImpl implements AdTinhNangService {

    private final AdTinhNangRepository adTinhNangRepository;

    @Override
    public ResponseObject<?> getAllTinhNangs(AdFindTinhNangRequest request) {
        Pageable pageable = Helper.createPageable(request);

        return new ResponseObject<>(
                PageableObject.of(adTinhNangRepository.getAllTinhNangs(pageable, request)),
                HttpStatus.OK,
                "Lấy dữ liệu thành công."
        );
    }

    @Override
    public ResponseObject<?> getTinhNang(String id) {

        return adTinhNangRepository.findById(id)
                .map(ok -> new ResponseObject<>(ok, HttpStatus.OK, "Lấy thông tin tính năng thành công."))
                .orElseGet(() -> new ResponseObject<>(null, HttpStatus.NOT_FOUND,
                        "Id: " + id + " không tồn tại"));
    }

    @Override
    public ResponseObject<?> getListTinhNang() {
        return new ResponseObject<>(adTinhNangRepository.getListTinhNang(),
                HttpStatus.OK,
                "Lấy dữ liệu thành công."
        );
    }

    @Override
    public ResponseObject<?> createTinhNang(@Valid AdCreateUpdateTinhNangRequest request) {

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

        TinhNang TinhNang = new TinhNang();
        Random random = new Random();
        String code;
        do {
            int number = random.nextInt(1000);
            code = String.format("TH%04d", number);
        } while (adTinhNangRepository.existsTinhNangByMaTinhNang(code));
        TinhNang.setMaTinhNang(code);
        TinhNang.setTen(request.getTen());
        TinhNang.setDeleted(false);
        TinhNang addedTinhNang = adTinhNangRepository.save(TinhNang);
        return new ResponseObject<>(addedTinhNang, HttpStatus.CREATED, "Tạo tính năng thành công.");
    }

    @Override
    public ResponseObject<?> updateTinhNang(String id, @Valid AdCreateUpdateTinhNangRequest request) {

        if (request.getTen() == null || request.getTen().isBlank()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên không được trống");
        } else {
            if (request.getTen().length() < 1 || request.getTen().length() > 255) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên phải từ 1 -> 255 ký tự");
            } else if (!checkName(request.getTen())) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên không được chứa ký tự đặc biệt.");
            } else if (adTinhNangRepository.existsTinhNangByTenNotId(request.getTen(), id) != null) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên đã tồn tại, xin vui lòng nhập lại.");
            }
        }

        Optional<TinhNang> TinhNang = adTinhNangRepository.findById(id)
                .map(TinhNang1 -> {
                    TinhNang1.setTen(request.getTen());
                    return adTinhNangRepository.save(TinhNang1);
                });
        return TinhNang
                .map(TinhNang1 -> new ResponseObject<>(TinhNang1, HttpStatus.OK,
                        "Cập nhật tính năng thành công."))
                .orElseGet(() -> new ResponseObject<>(null, HttpStatus.NOT_FOUND,
                        "tính năng không tồn tại."));
    }

    @Override
    public ResponseObject<?> deleted(String id) {
        Optional<TinhNang> TinhNang = adTinhNangRepository.findById(id)
                .map(TinhNang1 -> {
                    TinhNang1.setDeleted(true);
                    return adTinhNangRepository.save(TinhNang1);
                });
        return TinhNang
                .map(TinhNang1 -> new ResponseObject<>(null, HttpStatus.OK,
                        "Xóa tính năng thành công."))
                .orElseGet(() -> new ResponseObject<>(null, HttpStatus.NOT_FOUND,
                        "tính năng không tồn tại."));
    }

    public boolean checkName(String name) {
        String regex = "^[a-zA-Z0-9àáãạảăắằẳẵặâấầẩẫậèéẹẻẽêềếểễệđìíĩỉịòóõọỏôốồổỗộơớờởỡợùúũụủư" +
                       "ứừửữựỳỵỷỹýÀÁÃẠẢĂẮẰẲẴẶÂẤẦẨẪẬÈÉẸẺẼÊỀẾỂỄỆĐÌÍĨỈỊÒÓÕỌỎÔỐỒỔỖỘƠỚỜỞỠỢÙÚŨỤỦƯỨỪỬỮỰỲỴỶỸÝ\\s]+$";
        return name != null && name.matches(regex);
    }
}
