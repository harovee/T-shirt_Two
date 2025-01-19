package com.shop.server.core.admin.bill.model.request;

import com.shop.server.core.common.base.PageableRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdminUpdateBillRequest extends PageableRequest {
    private String trangThai;

    private String idKhachHang;

    private String idPhieuGiamGia;

    private String diaChiNguoiNhan;

    private String tenNguoiNhan;

    private String soDienThoai;

    private String ghiChu;
}
