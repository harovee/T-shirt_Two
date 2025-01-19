package com.shop.server.core.admin.kich_co.service.impl;

import com.shop.server.core.admin.kich_co.model.request.AdCreateUpdateKichCoRequest;
import com.shop.server.core.admin.kich_co.model.request.AdFindKichCoRequest;
import com.shop.server.core.admin.kich_co.repository.AdKichCoRepository;
import com.shop.server.core.admin.kich_co.service.AdKichCoService;
import com.shop.server.core.common.base.PageableObject;
import com.shop.server.core.common.base.ResponseObject;
import com.shop.server.entities.main.KichCo;
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
public class AdKichCoServiceImpl implements AdKichCoService {

    private final AdKichCoRepository adKichCoRepository;

    @Override
    public ResponseObject<?> getAllKichCos(AdFindKichCoRequest request) {
        Pageable pageable = Helper.createPageable(request);

        return new ResponseObject<>(
                PageableObject.of(adKichCoRepository.getAllKichCos(pageable, request)),
                HttpStatus.OK,
                "Lấy dữ liệu thành công."
        );
    }

    @Override
    public ResponseObject<?> getKichCo(String id) {

        return adKichCoRepository.findById(id)
                .map(ok -> new ResponseObject<>(ok, HttpStatus.OK, "Lấy thông tin kích cỡ thành công."))
                .orElseGet(() -> new ResponseObject<>(null, HttpStatus.NOT_FOUND,
                        "Id: " + id + " không tồn tại"));
    }

    @Override
    public ResponseObject<?> getListKichCo() {
        return new ResponseObject<>(adKichCoRepository.getListKichCo(),
                HttpStatus.OK,
                "Lấy dữ liệu thành công."
        );
    }

    @Override
    public ResponseObject<?> createKichCo(@Valid AdCreateUpdateKichCoRequest request) {

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
        if (request.getChieuCaoMin() < 0) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Chiều cao nhỏ nhất phải lớn hơn 0");
        } else if (request.getChieuCaoMin() < 0){
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Chiều cao lớn nhất phải lớn hơn 0");
        } else if (request.getCanNangMin() < 0){
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Cân nặng nhỏ nhất phải lớn hơn 0");
        } else if (request.getCanNangMax() < 0){
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Cân nặng lớn nhất phải lớn hơn 0");
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
        kichCo.setChieuCaoMin(request.getChieuCaoMin());
        kichCo.setChieuCaoMax(request.getChieuCaoMax());
        kichCo.setCanNangMin(request.getCanNangMin());
        kichCo.setCanNangMax(request.getCanNangMax());
        kichCo.setDeleted(false);
        KichCo addedKichCo = adKichCoRepository.save(kichCo);
        return new ResponseObject<>(addedKichCo, HttpStatus.CREATED, "Tạo kích cỡ thành công.");
    }

    @Override
    public ResponseObject<?> updateKichCo(String id, @Valid AdCreateUpdateKichCoRequest request) {

        if (request.getTen() == null || request.getTen().isBlank()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên không được trống");
        } else {
            if (request.getTen().length() < 1 || request.getTen().length() > 255) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên phải từ 1 -> 255 ký tự");
            } else if (!checkName(request.getTen())) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên không được chứa ký tự đặc biệt.");
            } else if (adKichCoRepository.existsKichCoByTenNotId(request.getTen(), id) != null) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên đã tồn tại, xin vui lòng nhập lại.");
            }
        }

        if (request.getChieuCaoMin() < 0) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Chiều cao nhỏ nhất phải lớn hơn 0");
        } else if (request.getChieuCaoMin() < 0){
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Chiều cao lớn nhất phải lớn hơn 0");
        } else if (request.getCanNangMin() < 0){
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Cân nặng nhỏ nhất phải lớn hơn 0");
        } else if (request.getCanNangMax() < 0){
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Cân nặng lớn nhất phải lớn hơn 0");
        }

        Optional<KichCo> kichCo = adKichCoRepository.findById(id)
                .map(kichCo1 -> {
                    kichCo1.setTen(request.getTen());
                    kichCo1.setChieuCaoMin(request.getChieuCaoMin());
                    kichCo1.setChieuCaoMax(request.getChieuCaoMax());
                    kichCo1.setCanNangMin(request.getCanNangMin());
                    kichCo1.setCanNangMax(request.getCanNangMax());
                    return adKichCoRepository.save(kichCo1);
                });
        return kichCo
                .map(kichCo1 -> new ResponseObject<>(kichCo1, HttpStatus.OK,
                        "Cập nhật kích cỡ thành công."))
                .orElseGet(() -> new ResponseObject<>(null, HttpStatus.NOT_FOUND,
                        "kích cỡ không tồn tại."));
    }

    @Override
    public ResponseObject<?> deleted(String id) {
        Optional<KichCo> KichCo = adKichCoRepository.findById(id)
                .map(KichCo1 -> {
                    KichCo1.setDeleted(true);
                    return adKichCoRepository.save(KichCo1);
                });
        return KichCo
                .map(KichCo1 -> new ResponseObject<>(null, HttpStatus.OK,
                        "Xóa kích cỡ thành công."))
                .orElseGet(() -> new ResponseObject<>(null, HttpStatus.NOT_FOUND,
                        "kích cỡ không tồn tại."));
    }

    public boolean checkName(String name) {
        String regex = "^[a-zA-Z0-9àáãạảăắằẳẵặâấầẩẫậèéẹẻẽêềếểễệđìíĩỉịòóõọỏôốồổỗộơớờởỡợùúũụủư" +
                       "ứừửữựỳỵỷỹýÀÁÃẠẢĂẮẰẲẴẶÂẤẦẨẪẬÈÉẸẺẼÊỀẾỂỄỆĐÌÍĨỈỊÒÓÕỌỎÔỐỒỔỖỘƠỚỜỞỠỢÙÚŨỤỦƯỨỪỬỮỰỲỴỶỸÝ\\s]+$";
        return name != null && name.matches(regex);
    }
}
