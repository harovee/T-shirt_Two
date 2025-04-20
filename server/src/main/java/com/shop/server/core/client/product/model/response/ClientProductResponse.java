package com.shop.server.core.client.product.model.response;

import com.shop.server.core.client.product.model.response.thuoc_tinh.ChatLieuResponse;
import com.shop.server.core.client.product.model.response.thuoc_tinh.CoAoResponse;
import com.shop.server.core.client.product.model.response.thuoc_tinh.ColorResponse;
import com.shop.server.core.client.product.model.response.thuoc_tinh.DanhMucResponse;
import com.shop.server.core.client.product.model.response.thuoc_tinh.HoaTietResponse;
import com.shop.server.core.client.product.model.response.thuoc_tinh.ImageResponse;
import com.shop.server.core.client.product.model.response.thuoc_tinh.KieuDangResponse;
import com.shop.server.core.client.product.model.response.thuoc_tinh.SizeResponse;
import com.shop.server.core.client.product.model.response.thuoc_tinh.TayAoResponse;
import com.shop.server.core.client.product.model.response.thuoc_tinh.ThuongHieuResponse;
import com.shop.server.core.client.product.model.response.thuoc_tinh.TinhNangResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClientProductResponse {

    private String catalog;

    private String id;

    private List<String> maSPCTs;

    private String moTa;

    private  List<ImageResponse> anh;

    private String maSanPham;

    private String ten;

    private DanhMucResponse danhMuc;

    private ChatLieuResponse chatLieu;

    private TayAoResponse tayAo;

    private CoAoResponse coAo;

    private HoaTietResponse hoaTiet;

    private TinhNangResponse tinhNang;

    private KieuDangResponse kieuDang;

    private ThuongHieuResponse thuongHieu;

    private List<BigDecimal> gia;

    private List<BigDecimal> discount;

    private List<SizeResponse> kichCo;

    private List<ColorResponse> color;

    private List<Double> phanTramGiam;

    private Integer tongSoLuongBan;

}
