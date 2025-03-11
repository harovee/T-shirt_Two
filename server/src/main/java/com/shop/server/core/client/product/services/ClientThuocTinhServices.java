package com.shop.server.core.client.product.services;

import com.shop.server.core.common.base.ResponseObject;
import org.springframework.stereotype.Service;

@Service
public interface ClientThuocTinhServices {
    ResponseObject<?> getDanhMuc();

    ResponseObject<?> getChatLieu();

    ResponseObject<?> getColor();

    ResponseObject<?> getThuongHieu();

    ResponseObject<?> getKieuDang();
}
