package com.shop.server.core.admin.payment_method.model.response;

import com.shop.server.core.common.base.BaseResponse;

public interface AdminPaymentMethodResponse extends BaseResponse {
    String getId();

    String getTenPhuongThuc();
}
