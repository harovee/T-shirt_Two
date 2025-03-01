package com.shop.server.core.admin.phieugiamgia.services.Impl;

import com.shop.server.core.admin.phieugiamgia.model.request.AdVoucherKhachHangRequest;
import com.shop.server.core.admin.phieugiamgia.model.request.AdminKhachHangSearchRequest;
import com.shop.server.core.admin.phieugiamgia.model.request.PhieuGiamGiaRequest;
import com.shop.server.core.admin.phieugiamgia.model.request.PhieuGiamGiaSearchRequest;
import com.shop.server.core.admin.phieugiamgia.model.request.VoucherKhachHangRequest;
import com.shop.server.core.admin.phieugiamgia.model.response.PhieuGiamGiaResponse;
import com.shop.server.core.admin.phieugiamgia.repository.AdKhachHangPhieuGiamGiaRepository;
import com.shop.server.core.admin.phieugiamgia.repository.AdPhieuGiamGiaRepository;
import com.shop.server.core.admin.phieugiamgia.repository.AdminKhachHangRepository;
import com.shop.server.core.admin.phieugiamgia.services.AdPhieuGiamGiaMailService;
import com.shop.server.core.admin.phieugiamgia.services.AdPhieuGiamGiaServices;
import com.shop.server.core.common.base.PageableObject;
import com.shop.server.core.common.base.ResponseObject;
import com.shop.server.entities.main.KhachHang;
import com.shop.server.entities.main.KhachHangPhieuGiamGia;
import com.shop.server.entities.main.PhieuGiamGia;
import com.shop.server.utils.Helper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdPhieuGiamGiaServicesImpl implements AdPhieuGiamGiaServices {

    private final AdPhieuGiamGiaRepository adPhieuGiamGiaRepository;

    private final AdminKhachHangRepository adminKhachHangRepository;

    private final AdKhachHangPhieuGiamGiaRepository adKhachHangPhieuGiamGiaRepository;

    private final AdPhieuGiamGiaMailService adPhieuGiamGiaMailService;

    @Override
    public ResponseObject<?> getAllPhieuGiamGia(PhieuGiamGiaSearchRequest request) {
        Pageable pageable = PageRequest.of(request.getPage() - 1, request.getSize());
        return new ResponseObject<>(PageableObject.of(adPhieuGiamGiaRepository.searchPhieuGiamGiaByKey(request, pageable)), HttpStatus.OK, "Lấy danh sách phiếu giảm giá thành công");
    }

    @Override
    public ResponseObject<?> getPhieuGiamGiaById(String id) {
        Optional<PhieuGiamGiaResponse> optionalPhieuGiamGiaResponse = adPhieuGiamGiaRepository.getPhieuGiamGia(id);
        if (optionalPhieuGiamGiaResponse.isPresent()) {
            PhieuGiamGiaResponse response = optionalPhieuGiamGiaResponse.get();
            return new ResponseObject<>(optionalPhieuGiamGiaResponse.get(), HttpStatus.OK, "Lấy data thành công");
        } else return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "PhieuGiamGia không tìm thấy");
    }

    @Override
    public ResponseObject<PhieuGiamGia> createPhieuGiamGia(PhieuGiamGiaRequest request) {
        if (request == null) {
            return new ResponseObject<>(null, HttpStatus.BAD_REQUEST, "Dữ liệu không được để trống");
        }
        request.setMa(Helper.generateCode("PGG"));
        if (adPhieuGiamGiaRepository.existsPhieuGiamGiaByTen(request.getTen()) != null) {
            return new ResponseObject<>(null, HttpStatus.BAD_REQUEST, "Phiếu giảm giá đã tồn tại");
        }
        if (request.getSoLuong() == null || request.getSoLuong() <= 0) {
            return new ResponseObject<>(null, HttpStatus.BAD_REQUEST, "Số lượng phải lớn hơn 0");
        }
        if (request.getLoaiGiam()) {
            try {
                BigDecimal dieuKienGiam = BigDecimal.valueOf(Long.parseLong(request.getDieuKienGiam()));
                BigDecimal giaTriGiam = BigDecimal.valueOf(request.getGiaTriGiam());
                request.setGiamToiDa(String.valueOf(Long.parseLong(request.getDieuKienGiam())));
            } catch (Exception e) {
                return new ResponseObject<>(null, HttpStatus.BAD_REQUEST, "Điều kiện giảm và giảm tối đa sai kiểu dữ liệu");
            }
        } else {
            try {
                // request.setGiamToiDa(request.getGiaTriGiam());
                Long dieuKienGiam = Long.parseLong(request.getDieuKienGiam());
                Double giaTriGiam = request.getGiaTriGiam();
                request.setGiamToiDa(String.valueOf(BigDecimal.valueOf(888000)));
                if (giaTriGiam < 0 || giaTriGiam > 100) {
                    return new ResponseObject<>(null, HttpStatus.OK, "GiaTriGiam % nằm trong khoảng 0-100% ");
                }
            } catch (Exception e) {
                return new ResponseObject<>(null, HttpStatus.BAD_REQUEST, "Điều kiện giảm, giá trị giảm và giảm tối đa sai kiểu dữ liệu");
            }
        }
        PhieuGiamGia phieuGiamGia = new PhieuGiamGia();
        BeanUtils.copyProperties(request, phieuGiamGia);
        if (!phieuGiamGia.getKieu()) {
            for (KhachHang khachHang : adminKhachHangRepository.findAll()) {
                adPhieuGiamGiaMailService.sendMailCreateKhachHangVoucher(khachHang, phieuGiamGia);
            }
        }
        return new ResponseObject<>(adPhieuGiamGiaRepository.save(phieuGiamGia), HttpStatus.OK, "Thêm Phiếu giảm giá thành công");
    }


    @Override
    public ResponseObject<PhieuGiamGia> updatePhieuGiamGiaById(PhieuGiamGiaRequest request, String id) {
        if (request == null) {
            return new ResponseObject<>(null, HttpStatus.BAD_REQUEST, "Không để trống");
        }
        Optional<PhieuGiamGia> optionalPhieuGiamGia = adPhieuGiamGiaRepository.findById(id);
        if (optionalPhieuGiamGia.isPresent()) {
            if (adPhieuGiamGiaRepository.existPhieuGiamGiaById(id, request.getTen()) != null) {
                return new ResponseObject<>(null, HttpStatus.CONFLICT, "Tên phiếu giảm giá đã tồn tại");
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
                    return new ResponseObject<>(null, HttpStatus.BAD_REQUEST, "Điều kiện giảm và giảm tối đa sai kiểu dữ liệu");
                }
            } else {
                try {
                    Long giamToiDa = Long.parseLong(request.getGiamToiDa());
                    Long dieuKienGiam = Long.parseLong(request.getDieuKienGiam());
                    Double giaTriGiam = request.getGiaTriGiam();
                    if (giaTriGiam < 0 || giaTriGiam > 100) {
                        return new ResponseObject<>(null, HttpStatus.OK, "GiaTriGiam % nằm trong khoảng 0-100% ");
                    }
                    if (giamToiDa < (giaTriGiam / 100 * dieuKienGiam)) {
                        return new ResponseObject<>(null, HttpStatus.BAD_REQUEST, "Giá trị giảm tối đa không được nhỏ hơn giảm tối thiểu");
                    }
                } catch (Exception e) {
                    return new ResponseObject<>(null, HttpStatus.BAD_REQUEST, "Điều kiện giảm, giá trị giảm và giảm tối đa sai kiểu dữ liệu");
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
            phieuGiamGia.setKieu(request.getKieu());
            phieuGiamGia.setTrangThai(request.getTrangThai());
            if (!phieuGiamGia.getKieu()) {
                for (KhachHang khachHang : adminKhachHangRepository.findAll()) {
                    adPhieuGiamGiaMailService.sendMailUpdateKhachHangVoucher(khachHang, phieuGiamGia);
                }
            }
            return new ResponseObject<>(adPhieuGiamGiaRepository.save(phieuGiamGia), HttpStatus.OK, "Cập nhật phiếu giảm giá thành công");
        }
        return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "PhieuGiamGia không tìm thấy");
    }

    @Override
    public ResponseObject<?> deletePhieuGiamGiaById(String id) {
        Optional<PhieuGiamGia> optionalPhieuGiamGia = adPhieuGiamGiaRepository.findById(id);
        if (optionalPhieuGiamGia.isPresent()) {
            for (KhachHangPhieuGiamGia khachHangPhieuGiamGia : adKhachHangPhieuGiamGiaRepository.findByIdPhieuGiamGia(id)) {
                khachHangPhieuGiamGia.setDeleted(true);
                adKhachHangPhieuGiamGiaRepository.save(khachHangPhieuGiamGia);
            }
            PhieuGiamGia phieuGiamGia = optionalPhieuGiamGia.get();
            phieuGiamGia.setDeleted(true);
            adPhieuGiamGiaRepository.save(phieuGiamGia);
            return new ResponseObject<>(null, HttpStatus.OK, "Xóa phiếu giảm giá thành công");
        }
        return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "PhieuGiamGia không tìm thấy");
    }

    @Override
    public ResponseObject<?> getAllKhachHang(AdminKhachHangSearchRequest request) {
        Pageable pageable = Helper.createPageable(request);
        return new ResponseObject<>(PageableObject.of(adminKhachHangRepository.getListKhachHang(pageable, request)), HttpStatus.OK, "Lấy danh sách khách hàng thành công");
    }

    @Override
    @Transactional
    public ResponseObject<?> createVoucherKhachHang(PhieuGiamGiaRequest voucherRequest, VoucherKhachHangRequest khachHangVoucher) {
        try {
            if (khachHangVoucher.getIdKhachHangs() == null) {
                return ResponseObject.errorForward(HttpStatus.BAD_REQUEST, "Không có Khách hàng");
            }
            voucherRequest.setSoLuong((short) khachHangVoucher.getIdKhachHangs().size());
            ResponseObject<PhieuGiamGia> voucher = createPhieuGiamGia(voucherRequest);
            if (!voucher.isSuccess()) {
                return voucher;
            }
            khachHangVoucher.setIdPhieuGiamGia(voucher.getData().getId());
            for (String idKhachHang : khachHangVoucher.getIdKhachHangs()) {
                KhachHangPhieuGiamGia khachHangPhieuGiamGia = new KhachHangPhieuGiamGia();
                khachHangPhieuGiamGia.setKhachHang(adminKhachHangRepository.findById(idKhachHang).get());
                khachHangPhieuGiamGia.setPhieuGiamGia(adPhieuGiamGiaRepository.findById(khachHangVoucher.getIdPhieuGiamGia()).get());
                if (voucherRequest.getKieu()) {
                    adPhieuGiamGiaMailService.sendMailCreateKhachHangVoucher(khachHangPhieuGiamGia.getKhachHang(), khachHangPhieuGiamGia.getPhieuGiamGia());
                }
                adKhachHangPhieuGiamGiaRepository.save(khachHangPhieuGiamGia);
            }
            //adKhachHangPhieuGiamGiaRepository.saveVoucherAndCustomerVoucher(khachHangVoucher);
            return new ResponseObject<>(null, HttpStatus.OK, "Thêm phiếu giảm giá khách hàng thành công");
        } catch (RuntimeException e) {
            return new ResponseObject<>(null, HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public ResponseObject<?> getKhachHangByIdPhieuGiamGia(String idPhieuGiamGia) {
        return new ResponseObject<>(adminKhachHangRepository.getListKhachHangInVoucher(idPhieuGiamGia), HttpStatus.OK, "Lấy list khách hàng thành công");
    }

    @Override
    @Transactional
    public ResponseObject<?> updateVoucherKhachHang(String id, AdVoucherKhachHangRequest request) {
        try {
            List<String> idKhachHangs = request.getVoucherKhachHangRequest().getIdKhachHangs();
            if (idKhachHangs == null || idKhachHangs.isEmpty()) {
                return ResponseObject.errorForward(HttpStatus.BAD_REQUEST, "Không có Khách hàng");
            }

            // Cập nhật số lượng phiếu giảm giá
            request.getPhieuGiamGiaRequest().setSoLuong((short) idKhachHangs.size());
            ResponseObject<PhieuGiamGia> updatePhieuGiamGia = updatePhieuGiamGiaById(request.getPhieuGiamGiaRequest(), id);
            if (!updatePhieuGiamGia.isSuccess()) {
                return updatePhieuGiamGia;
            }
            PhieuGiamGia phieuGiamGia = adPhieuGiamGiaRepository.findById(id).orElseThrow();
            // Lấy danh sách khách hàng hiện có trong bảng trung gian
            List<KhachHangPhieuGiamGia> existingKhachHangs = adKhachHangPhieuGiamGiaRepository.findByIdPhieuGiamGia(id);
            Set<String> existingIds = existingKhachHangs.stream()
                    .filter(khpg -> !khpg.getDeleted())
                    .map(khpg -> khpg.getKhachHang().getId())
                    .collect(Collectors.toSet());

            Set<String> requestIds = new HashSet<>(idKhachHangs);

            // Cập nhật deleted = true cho những khách hàng không có trong request
            for (KhachHangPhieuGiamGia khpg : existingKhachHangs) {
                if (!requestIds.contains(khpg.getKhachHang().getId())) {
                    khpg.setDeleted(true);
                    if (phieuGiamGia.getKieu()){
                    adPhieuGiamGiaMailService.sendMailCancelKhachHangVoucher(khpg.getKhachHang(),phieuGiamGia);
                    }
                    adKhachHangPhieuGiamGiaRepository.save(khpg);
                } else if(phieuGiamGia.getKieu() && !existingIds.contains(khpg.getKhachHang().getId())){
                    adPhieuGiamGiaMailService.sendMailCreateKhachHangVoucher(khpg.getKhachHang(), phieuGiamGia);
                } else if(phieuGiamGia.getKieu()){
                    adPhieuGiamGiaMailService.sendMailUpdateKhachHangVoucher(khpg.getKhachHang(),phieuGiamGia);
                }
            }

            // Thêm mới khách hàng có trong request nhưng chưa có trong DB hoặc đã bị xóa mềm trước đó
            for (String idKhachHang : requestIds) {
                if (!existingIds.contains(idKhachHang)) {
                    KhachHangPhieuGiamGia khachHangPhieuGiamGia = new KhachHangPhieuGiamGia();
                    khachHangPhieuGiamGia.setPhieuGiamGia(phieuGiamGia);
                    khachHangPhieuGiamGia.setKhachHang(adminKhachHangRepository.findById(idKhachHang).orElseThrow());
                    khachHangPhieuGiamGia.setDeleted(false);
                    if (phieuGiamGia.getKieu()){
                    adPhieuGiamGiaMailService.sendMailCreateKhachHangVoucher(khachHangPhieuGiamGia.getKhachHang(), phieuGiamGia);
                    }
                    adKhachHangPhieuGiamGiaRepository.save(khachHangPhieuGiamGia);
                } else {
                    // Nếu đã tồn tại nhưng đang bị xóa mềm -> kích hoạt lại
                    KhachHangPhieuGiamGia khpg = existingKhachHangs.stream()
                            .filter(k -> k.getKhachHang().getId().equals(idKhachHang) && k.getDeleted())
                            .findFirst()
                            .orElse(null);
                    if (khpg != null) {
                        khpg.setDeleted(false); // Kích hoạt lại
                        adPhieuGiamGiaMailService.sendMailCreateKhachHangVoucher(khpg.getKhachHang(),phieuGiamGia);
                        adKhachHangPhieuGiamGiaRepository.save(khpg);
                    }
                }
            }
            return new ResponseObject<>(null, HttpStatus.OK, "Cập nhật phiếu giảm giá khách hàng thành công");
        } catch (RuntimeException e) {
            return new ResponseObject<>(null, HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    public ResponseObject<?> changeStatusPhieuGiamGia(String id, String trangThai) {
        Optional<PhieuGiamGia> optionalPhieuGiamGia = adPhieuGiamGiaRepository.findById(id);
        if (optionalPhieuGiamGia.isPresent()) {
            for (KhachHangPhieuGiamGia khachHangPhieuGiamGia : adKhachHangPhieuGiamGiaRepository.findByIdPhieuGiamGia(id)) {
                khachHangPhieuGiamGia.setDeleted(true);
                adKhachHangPhieuGiamGiaRepository.save(khachHangPhieuGiamGia);
            }
            PhieuGiamGia phieuGiamGia = optionalPhieuGiamGia.get();
            phieuGiamGia.setDeleted(true);
            phieuGiamGia.setTrangThai(trangThai);
            adPhieuGiamGiaRepository.save(phieuGiamGia);
            return new ResponseObject<>(null, HttpStatus.OK, "Cập nhật thành công");
        }
        return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "PhieuGiamGia không tìm thấy");
    }

}
