package com.shop.server.core.admin.sale.models.responses;

import com.shop.server.core.common.base.BaseResponse;

public interface AdminProductSaleModuleResponse extends BaseResponse {

    String getTen();

    String getTenDanhMuc();

    Integer getTongSoLuong();

}
