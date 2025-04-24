package com.shop.server.core.admin.san_pham_chi_tiet.service.impl;

import com.shop.server.core.admin.image.model.request.AdCreateUpdateAnhRequest;
import com.shop.server.core.admin.image.repository.AdAnhRepository;
import com.shop.server.core.admin.chat_lieu.repository.AdChatLieuRepository;
import com.shop.server.core.admin.co_ao.repository.AdCoAoRepository;
import com.shop.server.core.admin.hoa_tiet.repository.AdHoaTietRepository;
import com.shop.server.core.admin.kich_co.repository.AdKichCoRepository;
import com.shop.server.core.admin.kieu_dang.repository.AdKieuDangRepository;
import com.shop.server.core.admin.mau_sac.repository.AdMauSacRepository;
import com.shop.server.core.admin.product.repositories.AdminProductRepository;
import com.shop.server.core.admin.sale.repositories.AdminSaleProductRepository;
import com.shop.server.core.admin.san_pham_chi_tiet.model.request.AdCheckQuantityRequest;
import com.shop.server.core.admin.san_pham_chi_tiet.model.request.AdCreateUpdateSpctRequest;
import com.shop.server.core.admin.san_pham_chi_tiet.model.request.AdFindSpctRequest;
import com.shop.server.core.admin.san_pham_chi_tiet.model.request.AdUpdateSaleProductDetail;
import com.shop.server.core.admin.san_pham_chi_tiet.repository.AdSanPhamChiTietRepository;
import com.shop.server.core.admin.san_pham_chi_tiet.service.AdSanPhamChiTietService;
import com.shop.server.core.admin.tay_ao.repository.AdTayAoRepository;
import com.shop.server.core.admin.thuong_hieu.repository.AdThuongHieuRepository;
import com.shop.server.core.admin.tinh_nang.repository.AdTinhNangRepository;
import com.shop.server.core.common.base.PageableObject;
import com.shop.server.core.common.base.ResponseObject;
import com.shop.server.entities.main.Anh;
import com.shop.server.entities.main.DotGiamGia;
import com.shop.server.entities.main.SanPhamChiTiet;
import com.shop.server.entities.main.SanPhamGiamGia;
import com.shop.server.infrastructure.constants.module.Message;
import com.shop.server.infrastructure.constants.module.Status;
import com.shop.server.utils.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AdSanPhamChitietServiceImpl implements AdSanPhamChiTietService {

    private final AdSanPhamChiTietRepository adSanPhamChiTietRepository;

    private final AdChatLieuRepository adChatLieuRepository;

    private final AdCoAoRepository adCoAoRepository;

    private final AdHoaTietRepository adHoaTietRepository;

    private final AdKichCoRepository adKichCoRepository;

    private final AdKieuDangRepository adKieuDangRepository;

    private final AdMauSacRepository adMauSacRepository;

    private final AdTayAoRepository adTayAoRepository;

    private final AdThuongHieuRepository adThuongHieuRepository;

    private final AdTinhNangRepository adTinhNangRepository;

    private final AdminProductRepository adminProductRepository;

    private final AdAnhRepository adAnhRepository;

    private final AdminSaleProductRepository sanPhamGiamGiaRepository;


    @Override
    public ResponseObject<?> getSanPhamChiTiets(AdFindSpctRequest request) {
        Pageable pageable = Helper.createPageable(request);
        return new ResponseObject<>(
                PageableObject.of(adSanPhamChiTietRepository.getAllSanPhamChiTietsByIdSanPham(pageable, request)),
                HttpStatus.OK,
                "Lấy dữ liệu thành công."
        );
    }


    @Override
    public ResponseObject<?> getALlSanPhamChiTiets(AdFindSpctRequest request) {
        Pageable pageable = Helper.createPageable(request);
        return new ResponseObject<>(
                PageableObject.of(adSanPhamChiTietRepository.getAllSanPhamChiTiets(pageable, request)),
                HttpStatus.OK,
                "Lấy dữ liệu thành công."
        );
    }

    @Override
    public ResponseObject<?> getALlSanPhamChiTietOverZero(AdFindSpctRequest request) {
        Pageable pageable = Helper.createPageable(request, "ngay_tao", "asc");
        return new ResponseObject<>(
                PageableObject.of(adSanPhamChiTietRepository.getAllSanPhamChiTietOverZero(pageable, request)),
                HttpStatus.OK,
                "Lấy dữ liệu thành công."
        );
    }

    @Override
    public ResponseObject<?> getListSanPhamChitiet() {
        return new ResponseObject<>(
                adSanPhamChiTietRepository.getListSanPhamChiTiets(),
                HttpStatus.OK,
                "Lấy dữ liệu thành công."
        );
    }

    @Override
    public ResponseObject<?> getSanPhamChiTietById(String id) {
        return adSanPhamChiTietRepository.findById(id)
                .map(ok -> new ResponseObject<>(ok, HttpStatus.OK, "Lấy thông tin sản phẩm chi tiết thành công."))
                .orElseGet(() -> new ResponseObject<>(null, HttpStatus.NOT_FOUND,
                        "Id: " + id + " không tồn tại"));
    }

    @Transactional
    @Override
    public ResponseObject<?> createSanPhamChiTiet(AdCreateUpdateSpctRequest request) {
        if (request.getGia() == null || request.getGia().compareTo(BigDecimal.ZERO) < 0) {
            return new ResponseObject<>(null, HttpStatus.FOUND, "Giá không được nhỏ hơn 0");
        }
        if (request.getSoLuong() == null || request.getSoLuong() < 0) {
            System.out.println("Số lượng phải lớn hơn 0.");
            return new ResponseObject<>(null, HttpStatus.FOUND, "Số lượng không được nhỏ hơn 0");
        }
        Integer existingSpct = adSanPhamChiTietRepository.existsSanPhamChiTiet( request.getIdSanPham(),
                request.getIdKichCo(),
                request.getIdMauSac(),
                request.getIdChatLieu(),
                request.getIdCoAo(),
                request.getIdTayAo(),
                request.getIdHoaTiet(),
                request.getIdKieuDang(),
                request.getIdTinhNang(),
                request.getIdThuongHieu(),
                request.getGioiTinh());

        if (existingSpct ==1) {
            // Find the existing product detail
            SanPhamChiTiet existingProduct = adSanPhamChiTietRepository.findExistingSanPhamChiTiet(request);

            if (existingProduct != null) {
                // update số lượng và giá
                existingProduct.setSoLuong(existingProduct.getSoLuong() + request.getSoLuong());
                existingProduct.setGia(request.getGia());
                existingProduct.setGioiTinh(request.getGioiTinh());
                SanPhamChiTiet updatedSPCT = adSanPhamChiTietRepository.save(existingProduct);

                if (request.getListAnh().size() > 0 || request.getListAnh() != null) {
                    for (AdCreateUpdateAnhRequest anhRequest : request.getListAnh()) {
                        Anh anh = new Anh();
                        anh.setSanPhamChiTiet(updatedSPCT);
                        anh.setUrl(anhRequest.getUrl());
                        anh.setTen(anhRequest.getName());
                        anh.setDeleted(false);
                        adAnhRepository.save(anh);
                    }
                }

                return new ResponseObject<>(updatedSPCT, HttpStatus.OK, "Sản phẩm chi tiết đã tồn tại, đã cập nhật số lượng và giá.");
            }
        }
        SanPhamChiTiet spct = new SanPhamChiTiet();
        Random random = new Random();
        String code;
        do {
            int number = random.nextInt(1000000);
            code = String.format("SPCT%07d", number);
        } while (adSanPhamChiTietRepository.existsSanPhamChiTietByMaSPCT(code));
        spct.setMaSPCT(code);
        spct.setGia(request.getGia());
        spct.setSoLuong(request.getSoLuong());
        spct.setTrangThai(Status.ACTIVE);
        spct.setGioiTinh(request.getGioiTinh());
        spct.setSanPham(request.getIdSanPham() != null ? adminProductRepository.findById(request.getIdSanPham()).orElse(null) : null);
        spct.setChatLieu(request.getIdChatLieu() != null ? adChatLieuRepository.findById(request.getIdChatLieu()).orElse(null) : null);
        spct.setCoAo(request.getIdCoAo() != null ? adCoAoRepository.findById(request.getIdCoAo()).orElse(null) : null);
        spct.setHoaTiet(request.getIdHoaTiet() != null ? adHoaTietRepository.findById(request.getIdHoaTiet()).orElse(null) : null);
        spct.setKichCo(request.getIdKichCo() != null ? adKichCoRepository.findById(request.getIdKichCo()).orElse(null) : null);
        spct.setKieuDang(request.getIdKieuDang() != null ? adKieuDangRepository.findById(request.getIdKieuDang()).orElse(null) : null);
        spct.setMauSac(request.getIdMauSac() != null ? adMauSacRepository.findById(request.getIdMauSac()).orElse(null) : null);
        spct.setTayAo(request.getIdTayAo() != null ? adTayAoRepository.findById(request.getIdTayAo()).orElse(null) : null);
        spct.setThuongHieu(request.getIdThuongHieu() != null ? adThuongHieuRepository.findById(request.getIdThuongHieu()).orElse(null) : null);
        spct.setTinhNang(request.getIdTinhNang() != null ? adTinhNangRepository.findById(request.getIdTinhNang()).orElse(null) : null);
        spct.setDeleted(false);
        SanPhamChiTiet addedSPCT = adSanPhamChiTietRepository.save(spct);
        if (request.getListAnh().size() > 0 || request.getListAnh() != null) {
//            for (AdCreateUpdateAnhRequest anhRequest : request.getListAnh()) {
//                Anh anh = new Anh();
//                anh.setSanPhamChiTiet(addedSPCT);
//                anh.setUrl(anhRequest.getUrl());
//                anh.setTen(anhRequest.getName());
//
//                anh.setDeleted(false);
//                adAnhRepository.save(anh);
//            }
            for (int i = 0; i < request.getListAnh().size(); i++) {
                AdCreateUpdateAnhRequest anhRequest = request.getListAnh().get(i);

                Anh anh = new Anh();
                anh.setSanPhamChiTiet(addedSPCT);
                anh.setUrl(anhRequest.getUrl());
                anh.setTen(anhRequest.getName());
                anh.setDeleted(false);
                if (i == 0) {
                    anh.setIsTop(true);
                }
                adAnhRepository.save(anh);
            }
        }
        return new ResponseObject<>(addedSPCT, HttpStatus.CREATED, "Tạo sản phẩm chi tiết thành công.");
    }

    @Modifying
    @Transactional
    @Override
    public ResponseObject<?> updateSanPhamChiTiet(String id, AdCreateUpdateSpctRequest request) {
//        if (request.getGia() == null || request.getGia().compareTo(BigDecimal.ZERO) <= 0) {
//            return new ResponseObject<>(null, HttpStatus.FOUND, "Giá không được nhỏ hơn 0");
//        }
//        if (request.getSoLuong() == null || request.getSoLuong() <= 0) {
//            System.out.println("Số lượng phải lớn hơn 0.");
//            return new ResponseObject<>(null, HttpStatus.FOUND, "Số lượng không được nhỏ hơn 0");
//        }
//
//        Optional<SanPhamChiTiet> spct = adSanPhamChiTietRepository.findById(id)
//                .map(spct1 -> {
//                    spct1.setGia(request.getGia());
//                    spct1.setSoLuong(request.getSoLuong());
//                    spct1.setGioiTinh(request.getGioiTinh());
//                    spct1.setTrangThai(request.getTrangThai() == 0 ? Status.ACTIVE : Status.INACTIVE);
////                    spct1.setSanPham(adminProductRepository.findById(request.getIdSanPham()).orElse(null));
////                    spct1.setChatLieu(adChatLieuRepository.findById(request.getIdChatLieu()).orElse(null));
////                    spct1.setCoAo(adCoAoRepository.findById(request.getIdCoAo()).orElse(null));
////                    spct1.setHoaTiet(adHoaTietRepository.findById(request.getIdHoaTiet()).orElse(null));
////                    spct1.setKichCo(adKichCoRepository.findById(request.getIdKichCo()).orElse(null));
////                    spct1.setKieuDang(adKieuDangRepository.findById(request.getIdKieuDang()).orElse(null));
////                    spct1.setMauSac(adMauSacRepository.findById(request.getIdMauSac()).orElse(null));
////                    spct1.setTayAo(adTayAoRepository.findById(request.getIdTayAo()).orElse(null));
////                    spct1.setThuongHieu(adThuongHieuRepository.findById(request.getIdThuongHieu()).orElse(null));
////                    spct1.setTinhNang(adTinhNangRepository.findById(request.getIdTinhNang()).orElse(null));
//                    spct1.setSanPham(request.getIdSanPham() != null ? adminProductRepository.findById(request.getIdSanPham()).orElse(null) : null);
//                    spct1.setChatLieu(request.getIdChatLieu() != null ? adChatLieuRepository.findById(request.getIdChatLieu()).orElse(null) : null);
//                    spct1.setCoAo(request.getIdCoAo() != null ? adCoAoRepository.findById(request.getIdCoAo()).orElse(null) : null);
//                    spct1.setHoaTiet(request.getIdHoaTiet() != null ? adHoaTietRepository.findById(request.getIdHoaTiet()).orElse(null) : null);
//                    spct1.setKichCo(request.getIdKichCo() != null ? adKichCoRepository.findById(request.getIdKichCo()).orElse(null) : null);
//                    spct1.setKieuDang(request.getIdKieuDang() != null ? adKieuDangRepository.findById(request.getIdKieuDang()).orElse(null) : null);
//                    spct1.setMauSac(request.getIdMauSac() != null ? adMauSacRepository.findById(request.getIdMauSac()).orElse(null) : null);
//                    spct1.setTayAo(request.getIdTayAo() != null ? adTayAoRepository.findById(request.getIdTayAo()).orElse(null) : null);
//                    spct1.setThuongHieu(request.getIdThuongHieu() != null ? adThuongHieuRepository.findById(request.getIdThuongHieu()).orElse(null) : null);
//                    spct1.setTinhNang(request.getIdTinhNang() != null ? adTinhNangRepository.findById(request.getIdTinhNang()).orElse(null) : null);
//                    if (request.getListAnh() != null) {
//                        adAnhRepository.deleteAllByIdSanPhamChiTiet(id);
//                        for (AdCreateUpdateAnhRequest anhRequest : request.getListAnh()) {
//                            Anh anh = new Anh();
//                            anh.setTen(anhRequest.getName());
//                            anh.setUrl(anhRequest.getUrl());
//                            anh.setSanPhamChiTiet(spct1);
//                            anh.setDeleted(false);
//                            adAnhRepository.save(anh);
//                        }
//                    }
//                    return adSanPhamChiTietRepository.save(spct1);
//                });
//        return spct
//                .map(spct1 -> new ResponseObject<>(spct1, HttpStatus.OK,
//                        "Cập nhật sản phẩm chi tiết thành công."))
//                .orElseGet(() -> new ResponseObject<>(null, HttpStatus.NOT_FOUND,
//                        "Sản phẩm chi tiết không tồn tại."));
        if (request.getGia() == null || request.getGia().compareTo(BigDecimal.ZERO) <= 0) {
            return new ResponseObject<>(null, HttpStatus.FOUND, "Giá không được nhỏ hơn 0");
        }
        if (request.getSoLuong() == null || request.getSoLuong() <= 0) {
            System.out.println("Số lượng phải lớn hơn 0.");
            return new ResponseObject<>(null, HttpStatus.FOUND, "Số lượng không được nhỏ hơn 0");
        }

        // Check if the updated product would create a duplicate with another existing product
        Optional<SanPhamChiTiet> existingDuplicate = adSanPhamChiTietRepository.findExistingSanPhamChiTietExcluding(request, id);

        if (existingDuplicate.isPresent()) {
            // Option 1: Reject the update
            return new ResponseObject<>(null, HttpStatus.CONFLICT,
                    "Cập nhật không thành công. Sản phẩm chi tiết với các thuộc tính này đã tồn tại.");
        }

        Optional<SanPhamChiTiet> spct = adSanPhamChiTietRepository.findById(id)
                .map(spct1 -> {
                    spct1.setGia(request.getGia());
                    spct1.setSoLuong(request.getSoLuong());
                    spct1.setGioiTinh(request.getGioiTinh());
                    spct1.setTrangThai(request.getTrangThai() == 0 ? Status.ACTIVE : Status.INACTIVE);
                    spct1.setSanPham(request.getIdSanPham() != null ? adminProductRepository.findById(request.getIdSanPham()).orElse(null) : null);
                    spct1.setChatLieu(request.getIdChatLieu() != null ? adChatLieuRepository.findById(request.getIdChatLieu()).orElse(null) : null);
                    spct1.setCoAo(request.getIdCoAo() != null ? adCoAoRepository.findById(request.getIdCoAo()).orElse(null) : null);
                    spct1.setHoaTiet(request.getIdHoaTiet() != null ? adHoaTietRepository.findById(request.getIdHoaTiet()).orElse(null) : null);
                    spct1.setKichCo(request.getIdKichCo() != null ? adKichCoRepository.findById(request.getIdKichCo()).orElse(null) : null);
                    spct1.setKieuDang(request.getIdKieuDang() != null ? adKieuDangRepository.findById(request.getIdKieuDang()).orElse(null) : null);
                    spct1.setMauSac(request.getIdMauSac() != null ? adMauSacRepository.findById(request.getIdMauSac()).orElse(null) : null);
                    spct1.setTayAo(request.getIdTayAo() != null ? adTayAoRepository.findById(request.getIdTayAo()).orElse(null) : null);
                    spct1.setThuongHieu(request.getIdThuongHieu() != null ? adThuongHieuRepository.findById(request.getIdThuongHieu()).orElse(null) : null);
                    spct1.setTinhNang(request.getIdTinhNang() != null ? adTinhNangRepository.findById(request.getIdTinhNang()).orElse(null) : null);
                    if (request.getListAnh() != null) {
                        adAnhRepository.deleteAllByIdSanPhamChiTiet(id);
                        for (AdCreateUpdateAnhRequest anhRequest : request.getListAnh()) {
                            Anh anh = new Anh();
                            anh.setTen(anhRequest.getName());
                            anh.setUrl(anhRequest.getUrl());
                            anh.setSanPhamChiTiet(spct1);
                            anh.setDeleted(false);
                            adAnhRepository.save(anh);
                        }
                    }
                    return adSanPhamChiTietRepository.save(spct1);
                });
        updateProductSale(new AdUpdateSaleProductDetail(id, request.getGia()));
        return spct
                .map(spct1 -> new ResponseObject<>(spct1, HttpStatus.OK,
                        "Cập nhật sản phẩm chi tiết thành công."))
                .orElseGet(() -> new ResponseObject<>(null, HttpStatus.NOT_FOUND,
                        "Sản phẩm chi tiết không tồn tại."));
    }

    @Override
    public ResponseObject<?> deleted(String id) {
        Optional<SanPhamChiTiet> sanPhamChiTiet = adSanPhamChiTietRepository.findById(id)
                .map(spct -> {
                    spct.setDeleted(true);
                    return adSanPhamChiTietRepository.save(spct);
                });
        return sanPhamChiTiet
                .map(spct -> new ResponseObject<>(null, HttpStatus.OK,
                        "Xóa sản phẩm chi tiết thành công."))
                .orElseGet(() -> new ResponseObject<>(null, HttpStatus.NOT_FOUND,
                        "Sản phẩm chi tiết không tồn tại."));
    }

    ////All spct không phân trang
    @Override
    public ResponseObject<?> getAllProductDetail() {
        return new ResponseObject<>(adSanPhamChiTietRepository.getAllProductDetail(),
                HttpStatus.OK,
                "Lấy danh sách spct thành công");
    }
    ////All spct theo idSanPham không phân trang
    @Override
    public ResponseObject<?> getAllProductDetailByIdSanPham(String idSanPham) {
        return new ResponseObject<>(adSanPhamChiTietRepository.getAllProductDetailByIdSanPham(idSanPham),HttpStatus.OK,
                "Lấy danh sách spct theo sản phẩm thành công");
    }

    @Override
    public ResponseObject<?> checkQuantity(AdCheckQuantityRequest request) {
        if (adSanPhamChiTietRepository.checkQuantity(request) == 0) {
            return new ResponseObject<>(true, HttpStatus.OK, "Số lượng trong kho đủ!");
        } else {
            return new ResponseObject<>(false, HttpStatus.OK, "Số lượng trong kho không đủ!");
        }
    }

    @Override
    public ResponseObject<?> checkQuantityByIdSPCT(AdCheckQuantityRequest request) {
        if (adSanPhamChiTietRepository.checkQuantityByIdSPCT(request) == 0) {
            return new ResponseObject<>(true, HttpStatus.OK, "Số lượng trong kho đủ!");
        } else {
            return new ResponseObject<>(false, HttpStatus.OK, "Số lượng trong kho không đủ!");
        }
    }

    @Override
    public ResponseObject<?> checkQuantityInListProduct(List<AdCheckQuantityRequest> listRequest) {
        boolean check = false;
        for (AdCheckQuantityRequest request : listRequest) {
            if (adSanPhamChiTietRepository.checkQuantityInListProduct(request) == 0) {
                check = true;
            }
        }
        if (!check) {
            for (AdCheckQuantityRequest request : listRequest) {
                adSanPhamChiTietRepository.decreaseStockProduct(request.getId(), request.getQuantity());
            }
        }
        return new ResponseObject<>(check, HttpStatus.OK, check ? "Số lượng trong giỏ không đủ" : "Số lượng trong kho đủ!");
    }

    @Override
    public ResponseObject<?> plusQuantityInStockByListProduct(List<AdCheckQuantityRequest> listRequest) {
        for (AdCheckQuantityRequest request : listRequest) {
            adSanPhamChiTietRepository.plusStockProduct(request.getId(), request.getQuantity());
        }
        return new ResponseObject<>(null, HttpStatus.OK, "Update số lượng thành công");
    }

    @Override
    public ResponseObject<?> updateProductSale(AdUpdateSaleProductDetail request) {
        try {
            List<SanPhamGiamGia> sanPhamGiamGias = sanPhamGiamGiaRepository.findAllBySanPhamChiTietIdAndDeletedIsFalse(request.getId());
            sanPhamGiamGias.forEach((e) -> {
                e.setGiaSauGiam(giaSauGiam(e.getDotGiamGia(), request.getPrice()));
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
        return BigDecimal.valueOf(spct / 2);
    }
}
