package com.shop.server.core.admin.phieugiamgia.model.request;


import com.shop.server.core.common.base.PageableRequest;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;

@Getter
@Setter
public class PhieuGiamGiaSearchRequest extends PageableRequest {

    private String keyword;

    private String startDate;

    private String endDate;

    private Boolean loaiGiam;

    private String trangThai;
}
