package com.shop.server.core.client.product.services.Impl;

import com.shop.server.core.client.product.model.request.ClientProductDetailRequest;
import com.shop.server.core.client.product.model.request.ClientProductSearchRequest;
import com.shop.server.core.client.product.model.response.thuoc_tinh.ChatLieuResponse;
import com.shop.server.core.client.product.model.response.ClientProductProjectionResponse;
import com.shop.server.core.client.product.model.response.ClientProductResponse;
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
import com.shop.server.core.client.product.repository.ClientProductRepository;
import com.shop.server.core.client.product.services.ClientProductService;
import com.shop.server.core.common.base.PageableObject;
import com.shop.server.core.common.base.ResponseObject;
import com.shop.server.utils.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClientProductServiceImpl implements ClientProductService {
    private final ClientProductRepository clientProductRepository;

    @Override
    public ResponseObject<?> getAllProducts(ClientProductSearchRequest request) {
        Pageable pageable = Helper.createPageable(request);
        return new ResponseObject<>(PageableObject.of(getAllProducts(pageable,request)),
                HttpStatus.OK,
                "Lấy sản phẩm thành công");
    }
    @Override
    public ResponseObject<?> getProductById(String idSanPham, ClientProductDetailRequest request) {
        if (idSanPham == null || idSanPham.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.BAD_REQUEST, "idSanPham không tồn tại hoặc null");
        }
        return new ResponseObject<>(convertToResponse(clientProductRepository.getProductById(idSanPham,request)),HttpStatus.OK,
                "Lấy sản phẩm theo id thành công");
    }

    @Override
    public ResponseObject<?> getTop8Product(ClientProductSearchRequest request) {
        return ResponseObject.successForward(getTop8Products(request),
                "Lấy top 8 sản phẩm mới nhất thành công");
    }

    @Override
    public ResponseObject<?> getProductDetaiById(String idSanPham, ClientProductDetailRequest request) {
        if (idSanPham == null || idSanPham.isEmpty()) {
            return ResponseObject.errorForward(HttpStatus.BAD_REQUEST,"idSanPham null hoặc không tồn tại");
        }
        return new ResponseObject<>(convertToResponse(clientProductRepository.getProductDetailById(idSanPham,request)),HttpStatus.OK,
                "Lấy sản phẩm chi tiết thành công");
    }

    private Page<ClientProductResponse> getAllProducts(Pageable pageable, ClientProductSearchRequest request) {
        Page<ClientProductProjectionResponse> projections = clientProductRepository.getAllProducts(pageable, request);
        return projections.map(this::convertToResponse);
    }
    private List<ClientProductResponse> getTop8Products(ClientProductSearchRequest request) {
        List<ClientProductProjectionResponse> projections = clientProductRepository.getTop8Products(request);
        return projections.stream().map(this::convertToResponse).collect(Collectors.toList());
    }

    private ClientProductResponse convertToResponse(ClientProductProjectionResponse projection) {
        ClientProductResponse response = new ClientProductResponse();
        if (projection.getCatalog() != null) {
            response.setCatalog(projection.getCatalog());
        }
        response.setId(projection.getId());
        response.setMaSanPham(projection.getMaSanPham());
        response.setTen(projection.getTen());
        response.setDanhMuc(new DanhMucResponse(projection.getIdDanhMuc(),projection.getTenDanhMuc()));
        response.setChatLieu(new ChatLieuResponse(projection.getIdChatLieu(),projection.getTenChatLieu()));
        response.setTayAo(new TayAoResponse(projection.getIdTayAo(),projection.getTenTayAo()));
        response.setCoAo(new CoAoResponse(projection.getIdCoAo(),projection.getTenCoAo()));
        response.setHoaTiet(new HoaTietResponse(projection.getIdHoaTiet(), projection.getTenHoaTiet()));
        response.setTinhNang(new TinhNangResponse(projection.getIdTinhNang(),projection.getTenTinhNang()));
        response.setKieuDang(new KieuDangResponse(projection.getIdKieuDang(),projection.getTenKieuDang()));
        response.setThuongHieu(new ThuongHieuResponse(projection.getIdThuongHieu(),projection.getTenThuongHieu()));

        // Xử lý danh sách ảnh
        List<ImageResponse> images = processImages(projection.getAnhs());
        response.setAnh(images);

        // Xử lý danh sách giá
        List<BigDecimal> prices = Arrays.stream(projection.getGia().split(","))
                .map(String::trim)
                .map(BigDecimal::new)
                .collect(Collectors.toList());
        response.setGia(prices);

        if (projection.getdiscount() != null) {
        List<BigDecimal> giaGiam = Arrays.stream(projection.getdiscount().split(","))
                .map(String::trim)
                .map(BigDecimal::new)
                .collect(Collectors.toList());
        response.setDiscount(giaGiam);
        }
        // Xử lý danh sách kích cỡ
        List<SizeResponse> sizes = processSizes(projection.getKichCos());
        response.setKichCo(sizes);

        // Xử lý danh sách màu sắc
        List<ColorResponse> colors = processColors(projection.getColors());
        response.setColor(colors);

        return response;
    }

    private List<ImageResponse> processImages(String anhWithIds) {
        if (anhWithIds == null ) {
            return Collections.emptyList();
        }

        return Arrays.stream(anhWithIds.split(","))
                .map(String::trim)
                .map(pair -> {
                    String[] parts = pair.split(":");
                    return new ImageResponse(parts[0], parts[1]);
                })
                .collect(Collectors.toList());
    }

    private List<SizeResponse> processSizes(String sizesWithIds) {
        if (sizesWithIds == null) {
            return Collections.emptyList();
        }

        return Arrays.stream(sizesWithIds.split(","))
                .map(String::trim)
                .map(pair -> {
                    String[] parts = pair.split(":");
                    return new SizeResponse(parts[0], parts[1]);
                })
                .collect(Collectors.toList());
    }

    private List<ColorResponse> processColors(String colorWithIds) {
        if (colorWithIds == null) {
            return Collections.emptyList();
        }

        return Arrays.stream(colorWithIds.split(","))
                .map(String::trim)
                .map(pair -> {
                    String[] parts = pair.split(":");
                    return new ColorResponse(parts[0], parts[1],parts[2]);
                })
                .collect(Collectors.toList());
    }
}
