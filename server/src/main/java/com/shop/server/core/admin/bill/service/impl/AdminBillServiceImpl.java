package com.shop.server.core.admin.bill.service.impl;

import com.shop.server.core.admin.bill.model.request.AdminFindBillRequest;
import com.shop.server.core.admin.bill.model.request.AdminSaveBillRequest;
import com.shop.server.core.admin.bill.model.request.AdminUpdateBillRequest;
import com.shop.server.core.admin.bill.model.request.AdminUpdateBillWaitRequest;
import com.shop.server.core.admin.bill.repository.AdminBillRepository;
import com.shop.server.core.admin.bill.service.AdminBillService;
import com.shop.server.core.common.base.PageableObject;
import com.shop.server.core.common.base.ResponseObject;
import com.shop.server.entities.main.*;
import com.shop.server.infrastructure.constants.module.Message;
import com.shop.server.infrastructure.security.oauth2.session.InfoUserTShirt;
import com.shop.server.repositories.AddressRepository;
import com.shop.server.repositories.KhachHangRepository;
import com.shop.server.repositories.LichSuHoaDonRepository;
import com.shop.server.repositories.NhanVienRepository;
import com.shop.server.repositories.PhieuGiamGiaRepository;
import com.shop.server.utils.Helper;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.math.BigDecimal;
import java.util.*;

@Service
public class AdminBillServiceImpl implements AdminBillService {
    private final AdminBillRepository adminBillRepository;
    private final KhachHangRepository khachHangRepository;
    private final NhanVienRepository nhanVienRepository;
    private final PhieuGiamGiaRepository phieuGiamGiaRepository;
    private final LichSuHoaDonRepository lichSuHoaDonRepository;
    private final AddressRepository addressRepository;

    public AdminBillServiceImpl(AdminBillRepository adminBillRepository,
                                KhachHangRepository khachHangRepository,
                                NhanVienRepository nhanVienRepository,
                                PhieuGiamGiaRepository phieuGiamGiaRepository,
                                LichSuHoaDonRepository lichSuHoaDonRepository,
                                InfoUserTShirt infoUserTShirt, AddressRepository addressRepository) {
        this.adminBillRepository = adminBillRepository;
        this.khachHangRepository = khachHangRepository;
        this.nhanVienRepository = nhanVienRepository;
        this.phieuGiamGiaRepository = phieuGiamGiaRepository;
        this.lichSuHoaDonRepository = lichSuHoaDonRepository;
        this.addressRepository = addressRepository;
    }

    @Override
    public ResponseObject<?> getBills(AdminFindBillRequest request) {
        Pageable pageable = Helper.createPageable(request);
        return new ResponseObject<>(
                PageableObject.of(adminBillRepository.getBillsByRequest(pageable, request)),
                HttpStatus.OK,
                Message.Success.GET_SUCCESS
        );
    }

    @Override
    public ResponseObject<?> getBillsWait() {
        return new ResponseObject<>(
                adminBillRepository.getBillsWait(),
                HttpStatus.OK,
                Message.Success.GET_SUCCESS
        );
    }

    @Override
    public ResponseObject<?> getDetailBillById(String id) {
        return new ResponseObject<>(
                adminBillRepository.getDetailBillById(id),
                HttpStatus.OK,
                Message.Success.GET_SUCCESS
        );
    }

    @Override
    public ResponseObject<?> getBillRefundByMaHD(String maHoaDon) {
        return new ResponseObject<>(
                adminBillRepository.getDetailBillByMaOnRefund(maHoaDon),
                HttpStatus.OK,
                Message.Success.GET_SUCCESS
        );
    }

//    @Override
//    public ResponseObject<?> createBill(@Valid AdminSaveBillRequest request, BindingResult result) {
//        if (result.hasErrors()) {
//            return new ResponseObject<>(null, HttpStatus.BAD_REQUEST, Message.Error.CREATE_ERROR);
//        }
//        String maHD;
//        Random random = new Random();
//        do {
//            int number = random.nextInt(10000);
//            maHD = String.format("HD%04d", number);
//        } while (adminBillRepository.existsHoaDonByMa(maHD));
//
//        HoaDon hoaDon = new HoaDon();
//        hoaDon.setMa(maHD);
//        hoaDon.setLoaiHD(request.getLoaiHD());
//        if (hoaDon.getLoaiHD().equalsIgnoreCase("Online")) {
//            hoaDon.setTrangThai("Chờ xác nhận");
//        }else {
//            hoaDon.setTrangThai("Chờ thanh toán");
//        }
//
//
//        KhachHang kh = khachHangRepository.findById(request.getIdKhachHang())
//                .orElseThrow(() -> new RuntimeException("Khách hàng không tồn tại"));
//        hoaDon.setKhachHang(kh);
//        hoaDon.setSoDienThoai(kh.getPhoneNumber());
//        hoaDon.setTenNguoiNhan(kh.getFullName());
//        NhanVien nv = nhanVienRepository.findById(request.getIdNhanVien())
//                .orElseThrow(() -> new RuntimeException("Nhân viên không tồn tại"));
//        hoaDon.setNhanVien(nv);
//
//        PhieuGiamGia pgg = phieuGiamGiaRepository.findById(request.getIdPhieuGiamGia())
//                .orElseThrow(() -> new RuntimeException("Phiếu giảm giá không tồn tại"));
//        hoaDon.setPhieuGiamGia(pgg);
//
//        adminBillRepository.save(hoaDon);
//
////        LichSuHoaDon ls = new LichSuHoaDon();
////        ls.setIdHoaDon(hoaDon);
////        ls.setHanhDong("Tạo đơn hàng");
////        ls.setNguoiTao(request.getNguoiTao());
////        ls.setTrangThai(hoaDon.getTrangThai());
////        lichSuHoaDonRepository.save(ls);
//        return new ResponseObject<>(
//            null,
//            HttpStatus.OK,
//            Message.Success.CREATE_SUCCESS
//        );
//    }

    @Override
    public ResponseObject<?> createBill(AdminSaveBillRequest request) {
        String maHD;
        Random random = new Random();
        do {
            int number = random.nextInt(10000);
            maHD = String.format("HD%04d", number);
        } while (adminBillRepository.existsHoaDonByMa(maHD));

        HoaDon hoaDon = new HoaDon();
        hoaDon.setMa(maHD);
        hoaDon.setLoaiHD(request.getLoaiHD());
        if (hoaDon.getLoaiHD().equalsIgnoreCase("Online")) {
            hoaDon.setTrangThai("Chờ xác nhận");
        }else {
            hoaDon.setTrangThai("Hóa đơn chờ");
        }

        KhachHang kh = request.getIdKhachHang() != null ? khachHangRepository.findById(request.getIdKhachHang())
                .orElseThrow(() -> new RuntimeException("Khách hàng không tồn tại")) : null;
        hoaDon.setKhachHang(kh);

        NhanVien nv = request.getIdNhanVien() != null ? nhanVienRepository.findById(request.getIdNhanVien())
                .orElseThrow(() -> new RuntimeException("Nhân viên không tồn tại")) : null;
        hoaDon.setNhanVien(nv);

        PhieuGiamGia pgg = request.getIdPhieuGiamGia() != null ? phieuGiamGiaRepository.findById(request.getIdPhieuGiamGia())
                .orElseThrow(() -> new RuntimeException("Phiếu giảm giá không tồn tại")) : null;
        hoaDon.setPhieuGiamGia(pgg);

        adminBillRepository.save(hoaDon);

        LichSuHoaDon ls = new LichSuHoaDon();
        ls.setIdHoaDon(hoaDon);
        ls.setHanhDong("Tạo hóa đơn");
        ls.setMoTa(request.getMoTa());
        ls.setNguoiTao(request.getNguoiTao());
        ls.setTrangThai(hoaDon.getTrangThai());
        LichSuHoaDon ls1 = lichSuHoaDonRepository.save(ls);

        return new ResponseObject<>(
                null,
                HttpStatus.OK,
                Message.Success.CREATE_SUCCESS
        );
    }

    @Override
    public ResponseObject<?> updateBill(String id, AdminUpdateBillRequest request, BindingResult result) {
        if (result.hasErrors()) {
            return new ResponseObject<>(null, HttpStatus.BAD_REQUEST, Message.Error.UPDATE_ERROR);
        }
        HoaDon hoaDon = adminBillRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hóa đơn không tồn tại"));

        if (request.getIdKhachHang() != null) {
            KhachHang khachHang = khachHangRepository.findById(request.getIdKhachHang())
                    .orElseThrow(() -> new RuntimeException("Khách hàng không tồn tại"));
            hoaDon.setKhachHang(khachHang);
        }else {
            hoaDon.setKhachHang(hoaDon.getKhachHang());
        }

        hoaDon.setPhieuGiamGia(request.getIdPhieuGiamGia() != null ? phieuGiamGiaRepository.findById(request.getIdPhieuGiamGia()).orElse(null) : null);

        hoaDon.setDiaChiNguoiNhan(request.getDiaChiNguoiNhan());
        hoaDon.setTenNguoiNhan(request.getTenNguoiNhan());
        if (request.getSoDienThoai() != null) {
            hoaDon.setSoDienThoai(request.getSoDienThoai());
        }
//        else {
//            hoaDon.setSoDienThoai(khachHangRepository.findById(request.getIdKhachHang()).get().getPhoneNumber());
//        }
        hoaDon.setGhiChu(request.getGhiChu());
        HoaDon hd1 = adminBillRepository.save(hoaDon);

//        LichSuHoaDon ls = new LichSuHoaDon();
//        ls.setIdHoaDon(hoaDon);
//        ls.setHanhDong("Cập nhật thông tin hóa đơn");
//        ls.setMoTa(request.getMoTa());
//        ls.setNguoiTao(request.getNguoiTao());
//        ls.setTrangThai(hoaDon.getTrangThai());
//        LichSuHoaDon ls1 = lichSuHoaDonRepository.save(ls);

        return new ResponseObject<>(
                hd1,
                HttpStatus.OK,
                Message.Success.UPDATE_SUCCESS
        );
    }

    @Override
    public ResponseObject<?> changeStatusBill(String id, AdminUpdateBillRequest request) {
        HoaDon hoaDon = adminBillRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hóa đơn không tồn tại"));
        hoaDon.setTrangThai(request.getTrangThai());
        HoaDon hd1 = adminBillRepository.save(hoaDon);

        LichSuHoaDon ls = new LichSuHoaDon();
        ls.setIdHoaDon(hoaDon);
        ls.setHanhDong("Chuyển trạng thái hóa đơn -> '" + request.getTrangThai() + "'");
        ls.setMoTa(request.getMoTa());
        ls.setTrangThai(hoaDon.getTrangThai());
        LichSuHoaDon ls1 = lichSuHoaDonRepository.save(ls);

        return new ResponseObject<>(
                ls1,
                HttpStatus.OK,
                Message.Success.UPDATE_SUCCESS
        );
    }
  
    @Override
    public Map<String, Integer> getBillCountsByStatus() {
        List<Object[]> results = adminBillRepository.countBillsByStatus();
        Map<String, Integer> statusCounts = new HashMap<>();

        for (Object[] row : results) {
            String status = (String) row[0];
            Integer count = ((Number) row[1]).intValue();
            statusCounts.put(status, count);
        }

        return statusCounts;
    }

    @Transactional
    @Override
    public ResponseObject<?> removeBillWait(String id) {
        Optional<HoaDon> bill = adminBillRepository.findById(id);
        if (bill.isPresent()) {
            adminBillRepository.deleteByIdHoaDon(id);
            adminBillRepository.deleteLichSuByIdHoaDon(id);
            adminBillRepository.deletePMDByIdHoaDon(id);
            adminBillRepository.deleteById(id);
            return new ResponseObject<>(null, HttpStatus.OK, Message.Success.UPDATE_SUCCESS);
        } else {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Bill not found");
        }
    }

    @Override
    public ResponseObject<?> updateBillWait(String id, AdminUpdateBillWaitRequest request) {
        HoaDon hoaDon = adminBillRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hóa đơn không tồn tại"));

        if (request.getIdKhachHang() != null) {
            KhachHang khachHang = khachHangRepository.findById(request.getIdKhachHang())
                    .orElseThrow(() -> new RuntimeException("Khách hàng không tồn tại"));
            hoaDon.setKhachHang(khachHang);
        } else {
            hoaDon.setKhachHang(null);
        }

        hoaDon.setPhieuGiamGia(request.getIdPhieuGiamGia() != null ? phieuGiamGiaRepository.findById(request.getIdPhieuGiamGia()).orElse(null) : null);

        hoaDon.setDiaChiNguoiNhan(request.getDiaChiNguoiNhan());
        hoaDon.setTenNguoiNhan(request.getTenNguoiNhan());
        if (request.getSoDienThoai() != null) {
            hoaDon.setSoDienThoai(request.getSoDienThoai());
        }else {
            hoaDon.setSoDienThoai(null);
        }
        hoaDon.setGhiChu(request.getGhiChu());
        hoaDon.setTienGiam(request.getTienGiam() != null ? request.getTienGiam() : BigDecimal.valueOf(0));
        hoaDon.setTienShip(request.getTienShip() != null ? request.getTienShip() : BigDecimal.valueOf(0));
        hoaDon.setTongTien(request.getTongTien() != null ? request.getTongTien() : BigDecimal.valueOf(0));
        hoaDon.setTrangThai(request.getTrangThai());
        if (request.getIdPhieuGiamGia() != null) {
            adminBillRepository.updateQuantityVoucher(request.getIdPhieuGiamGia());
        }
        HoaDon hd1 = adminBillRepository.save(hoaDon);

        LichSuHoaDon ls = new LichSuHoaDon();
        ls.setIdHoaDon(hoaDon);
        ls.setHanhDong("Cập nhật hóa đơn");
        ls.setTrangThai(hoaDon.getTrangThai());
        lichSuHoaDonRepository.save(ls);

        return new ResponseObject<>(
                hd1,
                HttpStatus.OK,
                Message.Success.UPDATE_SUCCESS
        );
    }
}
