package com.shop.server.core.admin.san_pham_chi_tiet.model.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AdCheckQuantityRequest {
    private String id;

    private Long quantity;
}
