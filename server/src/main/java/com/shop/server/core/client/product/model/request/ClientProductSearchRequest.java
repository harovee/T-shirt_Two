package com.shop.server.core.client.product.model.request;

import com.shop.server.core.common.base.PageableRequest;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;


@Getter
@Setter
public class ClientProductSearchRequest extends PageableRequest {

    private String tenSanPham;

    private String tenChatLieu;

    private String tenThuongHieu;

    private String tenDanhMuc;

    private String tenKieuDang;

    private BigDecimal max;

    private BigDecimal min;
}
