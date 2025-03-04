package com.shop.server.core.client.product.services.Impl;

import com.shop.server.core.client.product.repository.ClientThuocTinhRepository;
import com.shop.server.core.client.product.services.ClientThuocTinhServices;
import com.shop.server.core.common.base.ResponseObject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientThuocTinhServiceImpl implements ClientThuocTinhServices {

    private final ClientThuocTinhRepository thuocTinhRepository;
    @Override
    public ResponseObject<?> getDanhMuc() {
        return ResponseObject.successForward(thuocTinhRepository.getDanhMuc(),
                "Lấy danh mục thành công");
    }

    @Override
    public ResponseObject<?> getChatLieu() {
        return ResponseObject.successForward(thuocTinhRepository.getChatLieu(),
                "Lấy chất liệu thành công");
    }

    @Override
    public ResponseObject<?> getColor() {
        return ResponseObject.successForward(thuocTinhRepository.getColor(),
                "Lấy màu sắc thành công");
    }

    @Override
    public ResponseObject<?> getThuongHieu() {
        return ResponseObject.successForward(thuocTinhRepository.getThuongHieu(),
                "Lấy thương hiệu thành công");
    }

    @Override
    public ResponseObject<?> getKieuDang() {
        return ResponseObject.successForward(thuocTinhRepository.getKieuDang(),
                "Lấy kiểu dáng thành công");
    }
}
