package com.shop.server.core.admin.kich_co.model.response;

import com.shop.server.core.common.base.BaseResponse;

public interface AdKichCoResponse extends BaseResponse {

    String getTen ();

    String getMaKichCo ();

    String getChieuCaoMin ();

    String getChieuCaoMax ();

    String getCanNangMin ();

    String getCanNangMax ();

    Long getNgayTao ();
}
