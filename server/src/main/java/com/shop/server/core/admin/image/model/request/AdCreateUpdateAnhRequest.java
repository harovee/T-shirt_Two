package com.shop.server.core.admin.image.model.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AdCreateUpdateAnhRequest {

    private String name;

    private String url;

    private String idSanPhamChiTiet;
}
