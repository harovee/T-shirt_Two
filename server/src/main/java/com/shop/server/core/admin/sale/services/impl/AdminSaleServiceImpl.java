package com.shop.server.core.admin.sale.services.impl;

import com.shop.server.core.admin.sale.models.requests.AdminFindProductDetailSaleModuleRequest;
import com.shop.server.core.admin.sale.models.requests.AdminFindProductSaleModuleRequest;
import com.shop.server.core.admin.sale.models.requests.AdminFindSaleProductDetailRequest;
import com.shop.server.core.admin.sale.models.requests.AdminFindSaleRequest;
import com.shop.server.core.admin.sale.models.requests.AdminSaleAndSaleProductDetailRequest;
import com.shop.server.core.admin.sale.models.requests.AdminSaleProductRequest;
import com.shop.server.core.admin.sale.models.requests.AdminSaleRequest;
import com.shop.server.core.admin.sale.models.responses.AdminDetailSaleResponse;
import com.shop.server.core.admin.sale.models.responses.AdminProductDetailSaleModuleResponse;
import com.shop.server.core.admin.sale.repositories.AdminProductSaleModuleRepository;
import com.shop.server.core.admin.sale.repositories.AdminSaleProductRepository;
import com.shop.server.core.admin.sale.repositories.AdminSaleRepository;
import com.shop.server.core.admin.sale.services.AdminSaleService;
import com.shop.server.core.common.base.PageableObject;
import com.shop.server.core.common.base.ResponseObject;
import com.shop.server.entities.main.DotGiamGia;
import com.shop.server.entities.main.SanPhamChiTiet;
import com.shop.server.entities.main.SanPhamGiamGia;
import com.shop.server.infrastructure.constants.module.Message;
import com.shop.server.repositories.SanPhamChiTietRepository;
import com.shop.server.utils.Helper;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Service
public class AdminSaleServiceImpl implements AdminSaleService {

    private final AdminSaleRepository adminSaleRepository;
    private final AdminProductSaleModuleRepository productSaleModuleRepository;
    private final AdminSaleProductRepository sanPhamGiamGiaRepository;
    private final SanPhamChiTietRepository sanPhamChiTietRepository;

    public AdminSaleServiceImpl(
            AdminSaleRepository adminSaleRepository,
            AdminProductSaleModuleRepository productSaleModuleRepository,
            AdminSaleProductRepository sanPhamGiamGiaRepository,
            SanPhamChiTietRepository sanPhamChiTietRepository) {
        this.adminSaleRepository = adminSaleRepository;
        this.productSaleModuleRepository = productSaleModuleRepository;
        this.sanPhamGiamGiaRepository = sanPhamGiamGiaRepository;
        this.sanPhamChiTietRepository = sanPhamChiTietRepository;
    }


    @Override
    public ResponseObject<?> getSales(AdminFindSaleRequest request) {

        Pageable pageable = Helper.createPageable(request, "ngay_tao", "desc");
        if (request.getSortBy() == null || request.getSortBy().isEmpty()) {
            request.setSortBy("ngay_tao");
        }
        return new ResponseObject<>(
                PageableObject.of(adminSaleRepository.getPromotionByRequest(pageable, request)),
                HttpStatus.OK,
                Message.Success.GET_SUCCESS
        );
    }

    @Override
    public ResponseObject<?> getSaleById(String id) {
        Optional<AdminDetailSaleResponse> detailSale = adminSaleRepository.findDetailSaleResponseById(id);
        if (detailSale.isEmpty()) {
            return ResponseObject.errorForward(
                    HttpStatus.NOT_FOUND,
                    Message.Error.GET_ERROR);
        } else {
            return ResponseObject.successForward(
                    detailSale.get(),
                    Message.Success.GET_SUCCESS);
        }
    }

    @Override
    public ResponseObject<DotGiamGia> createSale(AdminSaleRequest request) {
        try {
            DotGiamGia dotGiamGia = new DotGiamGia(
                    Helper.generateCode("DGG"),
                    request.getTen(),
                    request.getLoai(),
                    request.getGiaTri(),
                    request.getGiaTriGiamToiDa(),
                    request.getNgayBatDau(),
                    request.getNgayKetThuc(),
                    request.getTrangThai()
            );
            dotGiamGia.setDeleted(false);
            dotGiamGia = adminSaleRepository.save(dotGiamGia);
            return ResponseObject.successForward(
                    dotGiamGia,
                    Message.Success.CREATE_SUCCESS);
        } catch (Exception e) {
            return ResponseObject.errorForward(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    e.getMessage());
        }
    }

    @Override
    public ResponseObject<?> updateSale(String id, AdminSaleRequest request) {
        try {
            adminSaleRepository.updateSaleById(id, request);
            List<SanPhamGiamGia> sanPhamGiamGias = sanPhamGiamGiaRepository.findAllByDotGiamGiaIdAndDeletedIsFalse(id);
            sanPhamGiamGias.forEach((e) -> {
                e.setGiaSauGiam(giaSauGiam(e.getDotGiamGia(), e.getSanPhamChiTiet().getGia()));
                sanPhamGiamGiaRepository.save(e);
            });
            return ResponseObject.successForward(
                    "OK",
                    Message.Success.UPDATE_SUCCESS);
        } catch (Exception e) {
            return ResponseObject.errorForward(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    e.getMessage());
        }
    }

    @Override
    public ResponseObject<?> changeStatusSale(String id, String status) {
        try {
            adminSaleRepository.updateSaleStatusById(id, status);
            return ResponseObject.successForward(
                    "OK",
                    Message.Success.UPDATE_SUCCESS);
        } catch (Exception e) {
            return ResponseObject.errorForward(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    e.getMessage());
        }
    }

    @Override
    public ResponseObject<?> deleteSale(String id) {
        try {
            adminSaleRepository.deleteSaleById(id);
            return ResponseObject.successForward(
                    "OK",
                    Message.Success.DELETE_SUCCESS);
        } catch (Exception e) {
            return ResponseObject.errorForward(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    e.getMessage());
        }
    }

    @Override
    public ResponseObject<?> mapListAttributesOfProductDetail() {
        try {
            Map<String, List<?>> map = new HashMap<>();
            map.put("categories", productSaleModuleRepository.getCategories());
            map.put("brands", productSaleModuleRepository.getBrands());
            map.put("sizes", productSaleModuleRepository.getSizes());
            map.put("collars", productSaleModuleRepository.getCollars());
            map.put("sleeves", productSaleModuleRepository.getSleeves());
            map.put("vignettes", productSaleModuleRepository.getVignettes());
            map.put("materials", productSaleModuleRepository.getMaterials());
            map.put("features", productSaleModuleRepository.getFeatures());
            map.put("styles", productSaleModuleRepository.getStyles());

            return ResponseObject.successForward(map, Message.Success.GET_SUCCESS);
        } catch (Exception e) {
            return ResponseObject.errorForward(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }

    }

    @Override
    public ResponseObject<?> getProductDetails(AdminFindProductDetailSaleModuleRequest request) {
        Pageable pageable = Helper.createPageable(request);
        return new ResponseObject<>(
                PageableObject.of(productSaleModuleRepository.getProductDetailsByProductId(request, pageable)),
                HttpStatus.OK,
                Message.Success.GET_SUCCESS);
    }

    @Override
    public ResponseObject<?> getProducts(AdminFindProductSaleModuleRequest request) {
        Pageable pageable = Helper.createPageable(request);
        return new ResponseObject<>(
                PageableObject.of(productSaleModuleRepository.getProducts(request, pageable)),
                HttpStatus.OK,
                Message.Success.GET_SUCCESS
        );
    }

    @Override
    public ResponseObject<?> saveSaleProductDetails(AdminSaleProductRequest request) {
        try {
            if (request.getIdSanPhamChiTiets().isEmpty()) {
                return ResponseObject.errorForward(
                        HttpStatus.BAD_REQUEST,
                        Message.Validate.NOT_BLANK);
            }
            Optional<AdminDetailSaleResponse> dotGiamGia = adminSaleRepository.findDetailSaleResponseById(request.getIdDotGiamGia());
            if (dotGiamGia.isPresent()) {
                request.setLoaiGiamGia(dotGiamGia.get().getLoai());
                request.setGiaTriGiamGia(dotGiamGia.get().getGiaTri());
                adminSaleRepository.saveSaleProductDetails(request);
            } else {
                return ResponseObject.errorForward(HttpStatus.NOT_FOUND, "ID ĐỢT GIẢM GIÁ KHÔNG TỒN TẠI");
            }
            return ResponseObject.successForward("OK", Message.Success.CREATE_SUCCESS);
        } catch (Exception e) {
            return ResponseObject.errorForward(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public ResponseObject<?> saveSaleInfoAndSaleProductDetails(
            AdminSaleRequest saleRequest,
            AdminSaleProductRequest saleProductRequest) {
        try {
            ResponseObject<DotGiamGia> createdSale = createSale(saleRequest);
            if (!createdSale.isSuccess()) {
                return createdSale;
            }
            saleProductRequest.setIdDotGiamGia(createdSale.getData().getId());
            saleProductRequest.setNhanVien(createdSale.getData().getNguoiTao());
            saleProductRequest.setLoaiGiamGia(createdSale.getData().getLoai());
            saleProductRequest.setGiaTriGiamGia(createdSale.getData().getGiaTri());
            saleProductRequest.setGiaTriGiamToiDa(createdSale.getData().getGiaTriGiamToiDa());
            adminSaleRepository.saveSaleProductDetails(saleProductRequest);

            return ResponseObject.successForward("OK", Message.Success.CREATE_SUCCESS);
        } catch (Exception e) {
            return ResponseObject.errorForward(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public ResponseObject<?> getSaleProductDetailBySaleId(AdminFindSaleProductDetailRequest request) {
        Pageable pageable = Helper.createPageable(request, "ngay_tao", "asc");
        return new ResponseObject<>(
                PageableObject.of(adminSaleRepository.getProductDetailInSale(request, pageable)),
                HttpStatus.OK,
                Message.Success.GET_SUCCESS
        );
    }

    @Override
    public ResponseObject<?> deleteSaleProductById(String saleProductId) {
        try {
            Optional<SanPhamGiamGia> sanPhamGiamGia = sanPhamGiamGiaRepository.findById(saleProductId);
            if (sanPhamGiamGia.isEmpty()) {
                return ResponseObject.errorForward(HttpStatus.NOT_FOUND, Message.Response.NOT_FOUND);
            } else {
                SanPhamGiamGia sanPhamGiamGia1 = sanPhamGiamGia.get();
                sanPhamGiamGia1.setDeleted(true);
                sanPhamGiamGiaRepository.save(sanPhamGiamGia1);

                String productDetailId = sanPhamGiamGia1.getId();
                Optional<AdminProductDetailSaleModuleResponse> o = productSaleModuleRepository.getProductDetailById(productDetailId);
                return ResponseObject.successForward(o.orElse(null), Message.Success.DELETE_SUCCESS);
            }
        } catch (Exception e) {
            return ResponseObject.errorForward(HttpStatus.INTERNAL_SERVER_ERROR, Message.Response.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    @Override
    public ResponseObject<?> updateSaleAndSaveSaleProduct(String saleId, AdminSaleAndSaleProductDetailRequest request) {
        try {
            ResponseObject<?> savedSale = updateSale(saleId, request.getSaleRequest());
            if (!savedSale.isSuccess()) {
                return savedSale;
            }
            DotGiamGia dotGiamGia = adminSaleRepository.findById(saleId).orElseThrow();

            List<String> idProductDetails = request.getSaleProductRequest().getIdSanPhamChiTiets();
            idProductDetails.forEach((e) -> {
                SanPhamGiamGia sanPhamGiamGia;
                SanPhamChiTiet spct = sanPhamChiTietRepository.findById(e).orElseThrow();
                Optional<SanPhamGiamGia> sanPhamGiamGiaOptional =sanPhamGiamGiaRepository
                                .findBySanPhamChiTietIdAndDotGiamGiaIdAndDeletedIsFalse(spct.getId(), dotGiamGia.getId());
                if (sanPhamGiamGiaOptional.isEmpty()) {
                    sanPhamGiamGia = new SanPhamGiamGia(giaSauGiam(dotGiamGia, spct.getGia()), dotGiamGia, spct);
                    sanPhamGiamGia.setDeleted(false);
                } else {
                    sanPhamGiamGia = sanPhamGiamGiaOptional.get();
                    sanPhamGiamGia.setGiaSauGiam(giaSauGiam(dotGiamGia, spct.getGia()));
                }
                sanPhamGiamGiaRepository.save(sanPhamGiamGia);
            });
            return ResponseObject.successForward(HttpStatus.OK, Message.Success.UPDATE_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return ResponseObject.errorForward(HttpStatus.INTERNAL_SERVER_ERROR, Message.Response.INTERNAL_SERVER_ERROR);
        }
    }

    private BigDecimal giaSauGiam(DotGiamGia dotGiamGia, BigDecimal giaSPCT) {
        double spct = giaSPCT.doubleValue();
        String loai = dotGiamGia.getLoai();
        Double giaTriGiam = dotGiamGia.getGiaTri();
        Double giaTriGiamToiDa = dotGiamGia.getGiaTriGiamToiDa();

        if (loai.equals("PERCENT")) {
            return BigDecimal.valueOf(spct - (spct * (giaTriGiam / 100)));
        }
        if (loai.equals("VND") && spct >= giaTriGiam) {
            return BigDecimal.valueOf(spct - giaTriGiam);
        }
        if (loai.equals("VND") && spct < giaTriGiam && spct >= giaTriGiamToiDa) {
            return BigDecimal.valueOf(spct - giaTriGiamToiDa);
        }
        return BigDecimal.valueOf(spct/2);
    }


}
