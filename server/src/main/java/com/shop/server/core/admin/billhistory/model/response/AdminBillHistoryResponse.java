package com.shop.server.core.admin.billhistory.model.response;

import com.shop.server.core.common.base.BaseResponse;

public interface AdminBillHistoryResponse extends BaseResponse {
    String getId();

    String getMaHoaDon();

    String getHanhDong();

    String getMoTa();

    String getTrangThai();
}
