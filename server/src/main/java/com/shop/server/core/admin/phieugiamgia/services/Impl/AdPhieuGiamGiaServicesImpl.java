package com.shop.server.core.admin.phieugiamgia.services.Impl;

import com.shop.server.core.admin.phieugiamgia.model.request.PhieuGiamGiaRequest;
import com.shop.server.core.admin.phieugiamgia.model.request.PhieuGiamGiaSearchRequest;
import com.shop.server.core.admin.phieugiamgia.model.response.PhieuGiamGiaResponse;
import com.shop.server.core.admin.phieugiamgia.repository.AdPhieuGiamGiaRepository;
import com.shop.server.core.admin.phieugiamgia.services.AdPhieuGiamGiaServices;
import com.shop.server.core.common.base.PageableObject;
import com.shop.server.core.common.base.ResponseObject;
import com.shop.server.entities.main.PhieuGiamGia;
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
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdPhieuGiamGiaServicesImpl implements AdPhieuGiamGiaServices {

    private final AdPhieuGiamGiaRepository adPhieuGiamGiaRepository;

    @Override
    public ResponseObject<?> getAllPhieuGiamGia(PhieuGiamGiaSearchRequest request) {
        Pageable pageable = PageRequest.of(request.getPage()-1, request.getSize());
        if (request.getSearch().isBlank() || request.getSearch() == null) {
            return new ResponseObject<>(PageableObject.of(adPhieuGiamGiaRepository.getAllPhieuGiamGia(pageable)), HttpStatus.OK, "Get List success");
        } else {
            String key = "%" + request.getSearch().trim() + "%";
            return new ResponseObject<>(PageableObject.of(adPhieuGiamGiaRepository.searchPhieuGiamGiaByKey(key, pageable)), HttpStatus.OK, "Search Success");
        }
    }

    @Override
    public ResponseObject<?> getPhieuGiamGiaById(String id) {
        Optional<PhieuGiamGiaResponse> optionalPhieuGiamGiaResponse = adPhieuGiamGiaRepository.getPhieuGiamGia(id);
        if (optionalPhieuGiamGiaResponse.isPresent()) {
            return new ResponseObject<>(optionalPhieuGiamGiaResponse.get(), HttpStatus.OK, "Get PhieuGiamGia success");
        } else
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "PhieuGiamGia not found");
    }

    @Override
    public ResponseObject<?> createPhieuGiamGia(PhieuGiamGiaRequest request) {
        if (request == null) {
            return new ResponseObject<>(null, HttpStatus.BAD_REQUEST, "Not null");
        }
        request.setMa(Helper.generateCode("VC"));
        if (adPhieuGiamGiaRepository.existsPhieuGiamGiaByTen(request.getTen())) {
            return new ResponseObject<>(null, HttpStatus.BAD_REQUEST, "PhieuGiamGia đã tồn tại");
        }
        if (request.getNgayBatDau().isBefore(LocalDate.now()) || request.getNgayKetThuc().isBefore(LocalDate.now())) {
            return new ResponseObject<>(null, HttpStatus.BAD_REQUEST, "Ngày bắt đầu và ngày kết thúc phải ở tương lai.");
        }
        if (request.getSoLuong() < 0) {
            return new ResponseObject<>(null, HttpStatus.BAD_REQUEST, "So luong phai lon hon 0");
        }
        // loaiGiam false : % ,true : tiền mặt
        if (!request.getLoaiGiam()==false) {
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
                BigDecimal giamToiDa = BigDecimal.valueOf(Long.parseLong(request.getGiamToiDa()));
                BigDecimal dieuKienGiam = BigDecimal.valueOf(Long.parseLong(request.getDieuKienGiam()));
                Double giaTriGiam = request.getGiaTriGiam();
                if (giaTriGiam < 0 || giaTriGiam > 40) {
                    return new ResponseObject<>(null, HttpStatus.OK, "GiaTriGiam % nằm trong khoảng 0-40% ");
                }
                if (giamToiDa.compareTo(dieuKienGiam.multiply(BigDecimal.valueOf(giaTriGiam / 100))) > 0) {
                    return new ResponseObject<>(null, HttpStatus.BAD_REQUEST, "Giá trị giảm tối đa không được nhỏ hơn giá trị giảm tối thiểu");
                }
            } catch (Exception e) {
                return new ResponseObject<>(null, HttpStatus.BAD_REQUEST, "Điều kiện giảm, giá trị giảm và giảm tối đa sai kieu du lieu");
            }
        }
        if (request.getNgayBatDau().isAfter(request.getNgayKetThuc())) {
            return new ResponseObject<>(null, HttpStatus.BAD_REQUEST, "Ngày bắt đầu không được lớn hơn ngày kết thúc");
        }
        request.setTrangThai("ACTIVE");
        PhieuGiamGia phieuGiamGia = new PhieuGiamGia();
        BeanUtils.copyProperties(request, phieuGiamGia);
        return new ResponseObject<>(adPhieuGiamGiaRepository.save(phieuGiamGia), HttpStatus.OK, "Create PhieuGiamGia success");
    }

    @Override
    public ResponseObject<?> updatePhieuGiamGiaById(PhieuGiamGiaRequest request, String id) {
        if (request == null) {
            return new ResponseObject<>(null, HttpStatus.BAD_REQUEST, "Not null");
        }
        Optional<PhieuGiamGiaResponse> optionalPhieuGiamGia = adPhieuGiamGiaRepository.getPhieuGiamGia(id);
        if (optionalPhieuGiamGia.isPresent()) {
            if (adPhieuGiamGiaRepository.existPhieuGiamGiaById(id) != null) {
                return new ResponseObject<>(null, HttpStatus.CONFLICT, "PhieuGiamGia đã tồn tại");
            }

            if (request.getNgayBatDau().isBefore(LocalDate.now()) || request.getNgayKetThuc().isBefore(LocalDate.now())) {
                return new ResponseObject<>(null, HttpStatus.BAD_REQUEST, "Ngày bắt đầu và ngày kết thúc phải ở tương lai.");
            }
            if (request.getSoLuong() < 0) {
                return new ResponseObject<>(null, HttpStatus.BAD_REQUEST, "So luong phai lon hon 0");
            }
            if (request.getLoaiGiam()) {
                try {
                    BigDecimal giamToiDa = BigDecimal.valueOf(Long.parseLong(request.getGiamToiDa()));
                    BigDecimal dieuKienGiam = BigDecimal.valueOf(Long.parseLong(request.getDieuKienGiam()));
                    BigDecimal giaTriGiam = BigDecimal.valueOf(request.getGiaTriGiam());
                    if (giaTriGiam.compareTo(giamToiDa) > 0) {
                        return new ResponseObject<>(null, HttpStatus.BAD_REQUEST, "giaTriGiam khong duoc lon hon gia tri giamToiDa");
                    }
                } catch (Exception e) {
                    return new ResponseObject<>(null, HttpStatus.BAD_REQUEST, "Điều kiện giảm và giảm tối đa sai kieu du lieu");
                }
            } else {
                try {
                    BigDecimal giamToiDa = BigDecimal.valueOf(Long.parseLong(request.getGiamToiDa()));
                    BigDecimal dieuKienGiam = BigDecimal.valueOf(Long.parseLong(request.getDieuKienGiam()));
                    Double giaTriGiam = request.getGiaTriGiam();
                    if (giaTriGiam < 0 || giaTriGiam > 40) {
                        return new ResponseObject<>(null, HttpStatus.OK, "GiaTriGiam % nằm trong khoảng 0-40% ");
                    }
                    if (giamToiDa.compareTo(dieuKienGiam.multiply(BigDecimal.valueOf(giaTriGiam / 100))) > 0) {
                        return new ResponseObject<>(null, HttpStatus.BAD_REQUEST, "GiamToiDa không được nhỏ hon giảm tối thiểu");
                    }
                } catch (Exception e) {
                    return new ResponseObject<>(null, HttpStatus.BAD_REQUEST, "Điều kiện giảm, giá trị giảm và giảm tối đa sai kieu du lieu");
                }
            }
            if (request.getNgayBatDau().isAfter(request.getNgayKetThuc())) {
                return new ResponseObject<>(null, HttpStatus.BAD_REQUEST, "Ngày bắt đầu không được lớn hơn ngày kết thúc");
            }
            PhieuGiamGia phieuGiamGia = (PhieuGiamGia) optionalPhieuGiamGia.get();
            phieuGiamGia.setGiamToiDa(request.getGiamToiDa());
            phieuGiamGia.setLoaiGiam(request.getLoaiGiam());
            phieuGiamGia.setGiaTriGiam(request.getGiaTriGiam());
            phieuGiamGia.setDieuKienGiam(request.getDieuKienGiam());
            phieuGiamGia.setNgayBatDau(request.getNgayBatDau());
            phieuGiamGia.setNgayKetThuc(request.getNgayKetThuc());
            phieuGiamGia.setSoLuong(request.getSoLuong());
            phieuGiamGia.setTen(request.getTen());
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
}
