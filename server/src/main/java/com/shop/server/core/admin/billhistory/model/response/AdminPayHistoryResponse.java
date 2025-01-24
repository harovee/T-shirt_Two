package com.shop.server.core.admin.billhistory.model.response;

import com.shop.server.core.common.base.BaseResponse;

import java.math.BigDecimal;

public interface AdminPayHistoryResponse extends BaseResponse {
    String getMaGiaoDich();

    String getTenPhuongThuc();

    String getGhiChu();

    BigDecimal getTongTienHD();

    String getNguoiTao();

    Long getNgayTao();
}
