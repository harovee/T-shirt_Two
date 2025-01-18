package com.shop.server.core.admin.phieugiamgia.services.Impl;

import com.shop.server.core.admin.phieugiamgia.model.request.AdminKhachHangSearchRequest;
import com.shop.server.core.admin.phieugiamgia.model.request.AdminProductSearchRequest;
import com.shop.server.core.admin.phieugiamgia.model.request.AdminVoucherSanPhamKhachHangRequest;
import com.shop.server.core.admin.phieugiamgia.model.request.PhieuGiamGiaRequest;
import com.shop.server.core.admin.phieugiamgia.model.request.PhieuGiamGiaSearchRequest;
import com.shop.server.core.admin.phieugiamgia.model.request.VoucherSanPhamKhachHangRequest;
import com.shop.server.core.admin.phieugiamgia.model.response.AdKhachHangResponse;
import com.shop.server.core.admin.phieugiamgia.model.response.AdminProductDetailResponse;
import com.shop.server.core.admin.phieugiamgia.model.response.PhieuGiamGiaResponse;
import com.shop.server.core.admin.phieugiamgia.repository.AdPhieuGiamGiaRepository;
import com.shop.server.core.admin.phieugiamgia.repository.AdProductDetailRepository;
import com.shop.server.core.admin.phieugiamgia.repository.AdminKhachHangRepository;
import com.shop.server.core.admin.phieugiamgia.repository.AdminVoucherSanPhamKhachHangRepository;
import com.shop.server.core.admin.phieugiamgia.services.AdPhieuGiamGiaServices;
import com.shop.server.core.common.base.PageableObject;
import com.shop.server.core.common.base.ResponseObject;
import com.shop.server.entities.main.PhieuGiamGia;
import com.shop.server.entities.main.VoucherSanPhamKhachHang;
import com.shop.server.utils.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdPhieuGiamGiaServicesImpl implements AdPhieuGiamGiaServices {

    private final AdPhieuGiamGiaRepository adPhieuGiamGiaRepository;

    private final AdminKhachHangRepository adminKhachHangRepository;

    private final AdProductDetailRepository adProductDetailRepository;

    private final AdminVoucherSanPhamKhachHangRepository adminVoucherSanPhamKhachHangRepository;

    @Override
    public ResponseObject<?> getAllPhieuGiamGia(PhieuGiamGiaSearchRequest request) {
        Pageable pageable = PageRequest.of(request.getPage() - 1, request.getSize());
        return new ResponseObject<>(
                PageableObject.of(adPhieuGiamGiaRepository.searchPhieuGiamGiaByKey(request, pageable)),
                HttpStatus.OK, "Lấy data thành công");
    }

    @Override
    public ResponseObject<?> getPhieuGiamGiaById(String id) {
        Optional<PhieuGiamGiaResponse> optionalPhieuGiamGiaResponse = adPhieuGiamGiaRepository.getPhieuGiamGia(id);
        if (optionalPhieuGiamGiaResponse.isPresent()) {
            PhieuGiamGiaResponse response = optionalPhieuGiamGiaResponse.get();
            return new ResponseObject<>(optionalPhieuGiamGiaResponse.get(), HttpStatus.OK, "Get PhieuGiamGia success");
        } else
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "PhieuGiamGia not found");
    }

    @Override
    public ResponseObject<PhieuGiamGia> createPhieuGiamGia(PhieuGiamGiaRequest request) {
        if (request == null) {
            return new ResponseObject<>(null, HttpStatus.BAD_REQUEST, "Not null");
        }
        request.setMa(Helper.generateCode("VC"));
        if (adPhieuGiamGiaRepository.existsPhieuGiamGiaByTen(request.getTen()) != null) {
            return new ResponseObject<>(null, HttpStatus.BAD_REQUEST, "PhieuGiamGia đã tồn tại");
        }
        if (request.getNgayBatDau().isBefore(LocalDate.now().minusDays(1))) {
            return new ResponseObject<>(null, HttpStatus.BAD_REQUEST, "Ngày bắt đầu phải ở tương lai.");
        }
        if (request.getNgayBatDau().isAfter(request.getNgayKetThuc())) {
            return new ResponseObject<>(null, HttpStatus.BAD_REQUEST, "Ngày bắt đầu không được lớn hơn ngày kết thúc");
        }
        if (request.getSoLuong() < 0) {
            return new ResponseObject<>(null, HttpStatus.BAD_REQUEST, "So luong phai lon hon 0");
        }
        // loaiGiam false : % ,true : tiền mặt
        if (!request.getLoaiGiam() == false) {
            try {
                BigDecimal giamToiDa = BigDecimal.valueOf(Long.parseLong(request.getGiamToiDa()));
                BigDecimal dieuKienGiam = BigDecimal.valueOf(Long.parseLong(request.getDieuKienGiam()));
                BigDecimal giaTriGiam = BigDecimal.valueOf(request.getGiaTriGiam());
                if (giaTriGiam.compareTo(giamToiDa) > 0) {
                    return new ResponseObject<>(null, HttpStatus.BAD_REQUEST, "Giá trị giảm tối đa không được nhỏ hơn giá trị giảm");
                }
            } catch (Exception e) {
                return new ResponseObject<>(null, HttpStatus.BAD_REQUEST, "Điều kiện giảm và giảm tối đa sai kiểu dữ liệu");
            }
        } else {
            try {
                Long giamToiDa = Long.valueOf(Long.parseLong(request.getGiamToiDa()));
                Long dieuKienGiam = Long.valueOf(Long.parseLong(request.getDieuKienGiam()));
                Double giaTriGiam = request.getGiaTriGiam();
                if (giaTriGiam < 0 || giaTriGiam > 100) {
                    return new ResponseObject<>(null, HttpStatus.OK, "GiaTriGiam % nằm trong khoảng 0-40% ");
                }
                if (giamToiDa < (giaTriGiam * dieuKienGiam) / 100) {
                    return new ResponseObject<>(null, HttpStatus.BAD_REQUEST, "Giá trị giảm tối đa không được nhỏ hơn giá trị giảm tối thiểu");
                }
            } catch (Exception e) {
                return new ResponseObject<>(null, HttpStatus.BAD_REQUEST, "Điều kiện giảm, giá trị giảm và giảm tối đa sai kieu du lieu");
            }
        }
        if (request.getNgayBatDau().isAfter(request.getNgayKetThuc())) {
            return new ResponseObject<>(null, HttpStatus.BAD_REQUEST, "Ngày bắt đầu không được lớn hơn ngày kết thúc");
        }
        if (request.getNgayBatDau().isAfter(LocalDate.now())) {
            request.setTrangThai("NOT_STARTED");
        }
        if (Objects.equals(request.getNgayBatDau(), LocalDate.now())) {
            request.setTrangThai("ACTIVE");
        }
        PhieuGiamGia phieuGiamGia = new PhieuGiamGia();
        BeanUtils.copyProperties(request, phieuGiamGia);
        return new ResponseObject<>(adPhieuGiamGiaRepository.save(phieuGiamGia), HttpStatus.OK, "Create PhieuGiamGia success");
    }

    @Override
    public ResponseObject<?> updatePhieuGiamGiaById(PhieuGiamGiaRequest request, String id) {
        if (request == null) {
            return new ResponseObject<>(null, HttpStatus.BAD_REQUEST, "Not null");
        }
        Optional<PhieuGiamGia> optionalPhieuGiamGia = adPhieuGiamGiaRepository.findById(id);

        if (optionalPhieuGiamGia.isPresent()) {
            if (optionalPhieuGiamGia.get().getTrangThai().equals("ACTIVE") || optionalPhieuGiamGia.get().getTrangThai().equals("EXPIRED")) {
                return new ResponseObject<>(null, HttpStatus.BAD_REQUEST, "Không thể cập nhật phiếu giảm giá đang áp dụng hoặc đã hết hạn");
            }
            if (adPhieuGiamGiaRepository.existPhieuGiamGiaById(id, request.getTen()) != null) {
                return new ResponseObject<>(null, HttpStatus.CONFLICT, "PhieuGiamGia đã tồn tại");
            }

//            if (request.getNgayBatDau().isBefore(LocalDate.now())) {
//                return new ResponseObject<>(null, HttpStatus.BAD_REQUEST, "Ngày bắt đầu phải ở tương lai.");
//            }
            if (request.getNgayBatDau().isAfter(request.getNgayKetThuc())) {
                return new ResponseObject<>(null, HttpStatus.BAD_REQUEST, "Ngày bắt đầu không được lớn hơn ngày kết thúc");
            }
            if (request.getSoLuong() < 0) {
                return new ResponseObject<>(null, HttpStatus.BAD_REQUEST, "So luong phai lon hon 0");
            }
            if (request.getLoaiGiam()) {
                try {
                    BigDecimal dieuKienGiam = BigDecimal.valueOf(Long.parseLong(request.getDieuKienGiam()));
                    BigDecimal giaTriGiam = BigDecimal.valueOf(request.getGiaTriGiam());
                    request.setGiamToiDa(String.valueOf(Long.parseLong(request.getDieuKienGiam())));
                } catch (Exception e) {
                    return new ResponseObject<>(null, HttpStatus.BAD_REQUEST, "Điều kiện giảm và giảm tối đa sai kieu du lieu");
                }
            } else {
                try {
                    Long giamToiDa = Long.parseLong(request.getGiamToiDa());
                    Long dieuKienGiam = Long.parseLong(request.getDieuKienGiam());
                    Double giaTriGiam = request.getGiaTriGiam();
                    if (giaTriGiam < 0 || giaTriGiam > 40) {
                        return new ResponseObject<>(null, HttpStatus.OK, "GiaTriGiam % nằm trong khoảng 0-100% ");
                    }
                    if (giamToiDa < (giaTriGiam / 100 * dieuKienGiam)) {
                        return new ResponseObject<>(null, HttpStatus.BAD_REQUEST, "GiamToiDa không được nhỏ hon giảm tối thiểu");
                    }
                } catch (Exception e) {
                    return new ResponseObject<>(null, HttpStatus.BAD_REQUEST, "Điều kiện giảm, giá trị giảm và giảm tối đa sai kieu du lieu");
                }
            }
            PhieuGiamGia phieuGiamGia = optionalPhieuGiamGia.get();
            phieuGiamGia.setGiamToiDa(request.getGiamToiDa());
            phieuGiamGia.setLoaiGiam(request.getLoaiGiam());
            phieuGiamGia.setGiaTriGiam(request.getGiaTriGiam());
            phieuGiamGia.setDieuKienGiam(request.getDieuKienGiam());
            phieuGiamGia.setNgayBatDau(request.getNgayBatDau());
            phieuGiamGia.setNgayKetThuc(request.getNgayKetThuc());
            phieuGiamGia.setSoLuong(request.getSoLuong());
            phieuGiamGia.setTen(request.getTen());
            updateTrangThai();
            return new ResponseObject<>(adPhieuGiamGiaRepository.save(phieuGiamGia), HttpStatus.OK, "Update PhieuGiamGia success");
        }
        return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "PhieuGiamGia not found");
    }

    @Override
    public ResponseObject<?> deletePhieuGiamGiaById(String id) {
        Optional<PhieuGiamGia> optionalPhieuGiamGia = adPhieuGiamGiaRepository.findById(id);
        if (optionalPhieuGiamGia.isPresent()) {
            PhieuGiamGia phieuGiamGia = optionalPhieuGiamGia.get();
            phieuGiamGia.setDeleted(true);
            adPhieuGiamGiaRepository.save(phieuGiamGia);
            return new ResponseObject<>(null, HttpStatus.OK, "Delete PhieuGiamGia success");
        }
        return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "PhieuGiamGia not found");
    }

    @Override
    public ResponseObject<?> updateTrangThai() {
        LocalDate today = LocalDate.now();
        List<PhieuGiamGia> updatedDanhSachPhieu = adPhieuGiamGiaRepository.findAll()
                .stream()
                .peek(phieu -> {
                    if (phieu.getNgayKetThuc().isBefore(today)) {
                        phieu.setTrangThai("EXPIRED"); // Hết hạn
                    } else if (phieu.getNgayBatDau().isAfter(today)) {
                        phieu.setTrangThai("NOT_STARTED"); // Chưa bắt đầu
                    } else {
                        phieu.setTrangThai("ACTIVE"); // Đang hoạt động
                    }
                })
                .toList();
        adPhieuGiamGiaRepository.saveAll(updatedDanhSachPhieu);
        return new ResponseObject<>(updatedDanhSachPhieu, HttpStatus.OK, "Status updated successfully");
    }

    @Override
    public ResponseObject<?> getAllProductDetail(AdminProductSearchRequest request) {
        Pageable pageable = Helper.createPageable(request);
        return new ResponseObject<>(PageableObject.of(adProductDetailRepository.getAllProductDetail(pageable, request)), HttpStatus.OK, "Get All Product Detail success");
    }

    @Override
    public ResponseObject<?> getProductDetailById(String id) {
        Optional<AdminProductDetailResponse> optionalAdminProductDetailResponse = adProductDetailRepository.getProductDetailById(id);
        if (optionalAdminProductDetailResponse.isPresent()) {
            return new ResponseObject<>(optionalAdminProductDetailResponse.get(), HttpStatus.OK, "Get Product Detail success");
        }
        return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Get Product Detail not found");
    }

    @Override
    public ResponseObject<?> getAllKhachHang(AdminKhachHangSearchRequest request) {
        Pageable pageable = Helper.createPageable(request);
        return new ResponseObject<>(PageableObject.of(adminKhachHangRepository.getListKhachHang(pageable, request)), HttpStatus.OK, "Get All KhachHang success");
    }

    @Override
    public ResponseObject<?> getKhachHangById(String id) {
        Optional<AdKhachHangResponse> optionalAdKhachHangResponse = adminKhachHangRepository.getKhachHangById(id);
        if (optionalAdKhachHangResponse.isPresent()) {
            return new ResponseObject<>(optionalAdKhachHangResponse.get(), HttpStatus.OK, "Get KhachHang success");
        }
        return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Get KhachHang not found");
    }

    @Override
    public ResponseObject<?> createVoucherSanphamKhachHang(PhieuGiamGiaRequest requestVoucher,
                                                           VoucherSanPhamKhachHangRequest requestVoucherSanPhamKhachHang) {
        try {
            ResponseObject<PhieuGiamGia> createVoucher = createPhieuGiamGia(requestVoucher);
            if (!createVoucher.isSuccess()) {
                return createVoucher;
            }
            requestVoucherSanPhamKhachHang.setIdVoucher(createVoucher.getData().getId());
            adminVoucherSanPhamKhachHangRepository.saveVoucherSanPhamKhachHang(requestVoucherSanPhamKhachHang);
            return new ResponseObject<>(null, HttpStatus.OK, "Create Voucher success");
        } catch (RuntimeException e) {
            return new ResponseObject<>(null, HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
