package com.shop.server.core.admin.chat_lieu.service.impl;

import com.shop.server.core.admin.chat_lieu.model.request.AdCreateUpdateChatLieuRequest;
import com.shop.server.core.admin.chat_lieu.model.request.AdFindChatLieuRequest;
import com.shop.server.core.admin.chat_lieu.repository.AdChatLieuRepository;
import com.shop.server.core.admin.chat_lieu.service.AdChatLieuService;
import com.shop.server.core.common.base.PageableObject;
import com.shop.server.core.common.base.ResponseObject;
import com.shop.server.entities.main.ChatLieu;
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
public class AdChatLieuServiceImpl implements AdChatLieuService {

    private final AdChatLieuRepository adChatLieuRepository;

    @Override
    public ResponseObject<?> getAllChatLieus(AdFindChatLieuRequest request) {
        Pageable pageable = Helper.createPageable(request);

        return new ResponseObject<>(
                PageableObject.of(adChatLieuRepository.getAllChatLieus(pageable, request)),
                HttpStatus.OK,
                "Lấy dữ liệu thành công."
        );
    }

    @Override
    public ResponseObject<?> getChatLieu(String id) {

        return adChatLieuRepository.findById(id)
                .map(ok -> new ResponseObject<>(ok, HttpStatus.OK, "Lấy thông tin chất liệu thành công."))
                .orElseGet(() -> new ResponseObject<>(null, HttpStatus.NOT_FOUND,
                        "Id: " + id + " không tồn tại"));
    }

    @Override
    public ResponseObject<?> getListChatLieu() {
        return new ResponseObject<>(adChatLieuRepository.getListChatLieu(),
                HttpStatus.OK,
                "Lấy dữ liệu thành công."
        );
    }

    @Override
    public ResponseObject<?> createChatLieu(@Valid AdCreateUpdateChatLieuRequest request) {

        if (request.getTen() == null || request.getTen().isBlank()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên không được trống");
        } else {
            if (request.getTen().length() < 1 || request.getTen().length() > 255) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên phải từ 1 -> 255 ký tự");
            } else if (!checkName(request.getTen())) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên không được chứa ký tự đặc biệt.");
            } else if (adChatLieuRepository.existsChatLieuByTen(request.getTen())) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên đã tồn tại, xin vui lòng nhập lại.");
            }
        }

        ChatLieu chatLieu = new ChatLieu();
        Random random = new Random();
        String code;
        do {
            int number = random.nextInt(1000);
            code = String.format("CL%04d", number);
        } while (adChatLieuRepository.existsChatLieuByMaChatLieu(code));
        chatLieu.setMaChatLieu(code);
        chatLieu.setTen(request.getTen());
        chatLieu.setDeleted(false);
        ChatLieu addedChatLieu = adChatLieuRepository.save(chatLieu);
        return new ResponseObject<>(addedChatLieu, HttpStatus.CREATED, "Tạo chất liệu thành công.");
    }

    @Override
    public ResponseObject<?> updateChatLieu(String id, @Valid AdCreateUpdateChatLieuRequest request) {

        if (request.getTen() == null || request.getTen().isBlank()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên không được trống");
        } else {
            if (request.getTen().length() < 1 || request.getTen().length() > 255) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên phải từ 1 -> 255 ký tự");
            } else if (!checkName(request.getTen())) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên không được chứa ký tự đặc biệt.");
            } else if (adChatLieuRepository.existsChatLieuByTenNotId(request.getTen(), id) != null) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Tên đã tồn tại, xin vui lòng nhập lại.");
            }
        }

        Optional<ChatLieu> chatLieu = adChatLieuRepository.findById(id)
                .map(chatLieu1 -> {
                    chatLieu1.setTen(request.getTen());
                    return adChatLieuRepository.save(chatLieu1);
                });
        return chatLieu
                .map(chatLieu1 -> new ResponseObject<>(chatLieu1, HttpStatus.OK,
                        "Cập nhật chất liệu thành công."))
                .orElseGet(() -> new ResponseObject<>(null, HttpStatus.NOT_FOUND,
                        "Chất liệu không tồn tại."));
    }

    @Override
    public ResponseObject<?> deleted(String id) {
        Optional<ChatLieu> chatLieu = adChatLieuRepository.findById(id)
                .map(chatLieu1 -> {
                    chatLieu1.setDeleted(true);
                    return adChatLieuRepository.save(chatLieu1);
                });
        return chatLieu
                .map(chatLieu1 -> new ResponseObject<>(null, HttpStatus.OK,
                        "Xóa chất liệu thành công."))
                .orElseGet(() -> new ResponseObject<>(null, HttpStatus.NOT_FOUND,
                        "Chất liệu không tồn tại."));
    }

    public boolean checkName(String name) {
        String regex = "^[a-zA-Z0-9àáãạảăắằẳẵặâấầẩẫậèéẹẻẽêềếểễệđìíĩỉịòóõọỏôốồổỗộơớờởỡợùúũụủư" +
                       "ứừửữựỳỵỷỹýÀÁÃẠẢĂẮẰẲẴẶÂẤẦẨẪẬÈÉẸẺẼÊỀẾỂỄỆĐÌÍĨỈỊÒÓÕỌỎÔỐỒỔỖỘƠỚỜỞỠỢÙÚŨỤỦƯỨỪỬỮỰỲỴỶỸÝ\\s]+$";
        return name != null && name.matches(regex);
    }
}
