package com.shop.server.core.client.my_page.model.response;

import com.shop.server.core.common.base.BaseResponse;

import java.math.BigDecimal;

public interface ClientBillResponse extends BaseResponse {
    String getMa();

    BigDecimal getTongTien();

    Double getGiaTriGiam ();

    BigDecimal getTienGiam ();

    BigDecimal getPhiShip();

    BigDecimal getDaTra();

    BigDecimal getTienPhaiTra();

    String getTrangThai();

    String getPhuongThucNhan();

    String getLoaiDon();
}
