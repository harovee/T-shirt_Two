package com.shop.server.core.admin.point_of_sale.model.request;

import com.shop.server.core.common.base.PageableRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AdPOSFindProductRequest extends PageableRequest {

    private String maSanPham;

    private String tenSanPham;


}
