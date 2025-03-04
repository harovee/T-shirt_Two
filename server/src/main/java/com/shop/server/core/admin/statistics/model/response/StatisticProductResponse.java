package com.shop.server.core.admin.statistics.model.response;

import com.shop.server.core.common.base.BaseResponse;

public interface StatisticProductResponse extends BaseResponse {

    String getMaSanPham();

    String getTenSanPham();

    String getTenDanhMuc();

    Long getSoLuong();


}
