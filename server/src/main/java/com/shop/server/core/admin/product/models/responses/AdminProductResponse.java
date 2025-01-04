package com.shop.server.core.admin.product.models.responses;

import com.shop.server.core.common.base.BaseResponse;

public interface AdminProductResponse extends BaseResponse {

    String getTitle();

    String getArtist();

    Long getReleasedDate();

    Integer getStatus();

}