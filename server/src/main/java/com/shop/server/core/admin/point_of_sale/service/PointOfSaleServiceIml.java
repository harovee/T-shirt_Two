package com.shop.server.core.admin.point_of_sale.service;

import com.shop.server.core.admin.point_of_sale.model.request.AdPOSAddProductsToCartRequest;
import com.shop.server.core.admin.point_of_sale.model.request.AdPOSFindProductRequest;
import com.shop.server.core.admin.point_of_sale.model.request.AdPOSUpdateCartRequest;
import com.shop.server.core.admin.point_of_sale.repository.POSOrderDetailRepository;
import com.shop.server.core.admin.point_of_sale.repository.PointOfSaleRepository;
import com.shop.server.core.common.base.PageableObject;
import com.shop.server.core.common.base.ResponseObject;
import com.shop.server.entities.main.HoaDon;
import com.shop.server.infrastructure.constants.module.Message;
import com.shop.server.repositories.HoaDonRepository;
import com.shop.server.utils.Helper;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class PointOfSaleServiceIml implements PointOfSaleService {
  
    private final PointOfSaleRepository pointOfSaleRepository;
    private final POSOrderDetailRepository hoaDonChiTietRepository;
    private final HoaDonRepository hoaDonRepository;

    public PointOfSaleServiceIml(PointOfSaleRepository pointOfSaleRepository,
                                 POSOrderDetailRepository hoaDonChiTietRepository,
                                 HoaDonRepository hoaDonRepository) {
        this.pointOfSaleRepository = pointOfSaleRepository;
        this.hoaDonChiTietRepository = hoaDonChiTietRepository;
        this.hoaDonRepository = hoaDonRepository;
    }


    @Override
    public ResponseObject<?> getProductsInPOS(AdPOSFindProductRequest request) {
        Pageable pageable = Helper.createPageable(request);
        return new ResponseObject<>(
                PageableObject.of(pointOfSaleRepository.getProductDetails(request, pageable)),
                HttpStatus.OK,
                Message.Success.GET_SUCCESS
        );
    }

    @Override
    public ResponseObject<?> addProductsToCart(AdPOSAddProductsToCartRequest request) {
        try {
            HoaDon hoaDon = hoaDonRepository.findById(request.getIdHoaDonCho()).orElse(null);
            if (hoaDon == null) return ResponseObject.errorForward(HttpStatus.NOT_FOUND, Message.Response.NOT_FOUND + " : không tìm thấy hóa đơn thỏa mãn");

            hoaDonChiTietRepository.saveProductDetailsToCart(request);
            hoaDonChiTietRepository.updateExistingProductInCart(request);
            hoaDonChiTietRepository.decreaseStock(request.getIdSanPhamChiTiets(), request.getSoLuong());
            return ResponseObject.successForward(
                    getProductsInPendingOrder(request.getIdHoaDonCho()).getData(),
                    "Thêm sản phẩm thành công vào hóa đơn");
        } catch (Exception exception) {
            exception.printStackTrace(System.err);
            return ResponseObject.errorForward(HttpStatus.FORBIDDEN, exception.getMessage());
        }
    }

    @Override
    public ResponseObject<?> getProductsInPendingOrder(String idOrder) {
        return ResponseObject.successForward(
                pointOfSaleRepository.getProductsInOrder(idOrder),
                Message.Success.GET_SUCCESS
        );
    }

    @Override
    public Object getPriceRank() {
        return pointOfSaleRepository.getPriceRank().orElse(null);
    }

    @Override
    public ResponseObject<?> updateQuantityProductInCart(AdPOSUpdateCartRequest request) {
        try {
            hoaDonChiTietRepository.updateQuantityProductDetail(request);
            hoaDonChiTietRepository.updateQuantityOrderDetail(request);
            return ResponseObject.successForward("", Message.Success.UPDATE_SUCCESS);
        }catch (Exception e) {
            e.printStackTrace(System.err);
            return ResponseObject.errorForward(HttpStatus.INTERNAL_SERVER_ERROR, Message.Response.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseObject<?> deleteOrderDetail(String idHDCT) {
        try {
            hoaDonChiTietRepository.updateProductQuantityAfterDelete(idHDCT);
            hoaDonChiTietRepository.deleteProductInCart(idHDCT);
            return ResponseObject.successForward("", Message.Success.DELETE_SUCCESS);
        }catch (Exception e) {
            e.printStackTrace(System.err);
            return ResponseObject.errorForward(HttpStatus.INTERNAL_SERVER_ERROR, Message.Response.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseObject<?> getTotalAmount(String idHoaDon) {
        Double totalAmount = hoaDonChiTietRepository.getTotalAmountByIdHoaDon(idHoaDon);
        return new ResponseObject<>(
                totalAmount,
                HttpStatus.OK,
                Message.Success.GET_SUCCESS
        );
    }

    @Override
    public ResponseObject<?> getListProducts(String idHoaDon) {
        return new ResponseObject<>(
                hoaDonChiTietRepository.findAll(),
                HttpStatus.OK,
                Message.Success.GET_SUCCESS
        );
    }
}
