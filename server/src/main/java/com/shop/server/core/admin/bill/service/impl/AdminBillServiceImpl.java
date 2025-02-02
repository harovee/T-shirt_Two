package com.shop.server.core.admin.bill.service.impl;

import com.shop.server.core.admin.bill.model.request.AdminFindBillRequest;
import com.shop.server.core.admin.bill.model.request.AdminSaveBillRequest;
import com.shop.server.core.admin.bill.model.request.AdminUpdateBillRequest;
import com.shop.server.core.admin.bill.repository.AdminBillRepository;
import com.shop.server.core.admin.bill.service.AdminBillService;
import com.shop.server.core.common.base.PageableObject;
import com.shop.server.core.common.base.ResponseObject;
import com.shop.server.entities.main.HoaDon;
import com.shop.server.entities.main.KhachHang;
import com.shop.server.entities.main.LichSuHoaDon;
import com.shop.server.entities.main.NhanVien;
import com.shop.server.entities.main.PhieuGiamGia;
import com.shop.server.infrastructure.constants.module.Message;
import com.shop.server.repositories.KhachHangRepository;
import com.shop.server.repositories.LichSuHoaDonRepository;
import com.shop.server.repositories.NhanVienRepository;
import com.shop.server.repositories.PhieuGiamGiaRepository;
import com.shop.server.utils.Helper;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class AdminBillServiceImpl implements AdminBillService {
    private final AdminBillRepository adminBillRepository;
    private final KhachHangRepository khachHangRepository;
    private final NhanVienRepository nhanVienRepository;
    private final PhieuGiamGiaRepository phieuGiamGiaRepository;

    public AdminBillServiceImpl(AdminBillRepository adminBillRepository, KhachHangRepository khachHangRepository, NhanVienRepository nhanVienRepository, PhieuGiamGiaRepository phieuGiamGiaRepository, LichSuHoaDonRepository lichSuHoaDonRepository) {
        this.adminBillRepository = adminBillRepository;
        this.khachHangRepository = khachHangRepository;
        this.nhanVienRepository = nhanVienRepository;
        this.phieuGiamGiaRepository = phieuGiamGiaRepository;
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
    public ResponseObject<?> getDetailBillById(String id) {
        return new ResponseObject<>(
                adminBillRepository.getDetailBillById(id),
                HttpStatus.OK,
                Message.Success.GET_SUCCESS
        );
    }

    @Override
    public ResponseObject<?> createBill(@Valid AdminSaveBillRequest request, BindingResult result) {
        if (result.hasErrors()) {
            return new ResponseObject<>(null, HttpStatus.BAD_REQUEST, Message.Error.CREATE_ERROR);
        }
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
            hoaDon.setTrangThai("Chờ thanh toán");
        }


        KhachHang kh = khachHangRepository.findById(request.getIdKhachHang())
                .orElseThrow(() -> new RuntimeException("Khách hàng không tồn tại"));
        hoaDon.setKhachHang(kh);
        hoaDon.setSoDienThoai(kh.getPhoneNumber());
        hoaDon.setTenNguoiNhan(kh.getFullName());
        NhanVien nv = nhanVienRepository.findById(request.getIdNhanVien())
                .orElseThrow(() -> new RuntimeException("Nhân viên không tồn tại"));
        hoaDon.setNhanVien(nv);

        PhieuGiamGia pgg = phieuGiamGiaRepository.findById(request.getIdPhieuGiamGia())
                .orElseThrow(() -> new RuntimeException("Phiếu giảm giá không tồn tại"));
        hoaDon.setPhieuGiamGia(pgg);

        adminBillRepository.save(hoaDon);

//        LichSuHoaDon ls = new LichSuHoaDon();
//        ls.setIdHoaDon(hoaDon);
//        ls.setHanhDong("Tạo đơn hàng");
//        ls.setNguoiTao(request.getNguoiTao());
//        ls.setTrangThai(hoaDon.getTrangThai());
//        lichSuHoaDonRepository.save(ls);
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
        }else {
            hoaDon.setSoDienThoai(khachHangRepository.findById(request.getIdKhachHang()).get().getPhoneNumber());
        }
        hoaDon.setGhiChu(request.getGhiChu());
        HoaDon hd1 = adminBillRepository.save(hoaDon);



//        LichSuHoaDon ls = new LichSuHoaDon();
//        ls.setIdHoaDon(hoaDon);
//        ls.setHanhDong("Cập nhật hóa đơn");
//        ls.setNguoiTao(request.getNguoiTao());
//        ls.setTrangThai(hoaDon.getTrangThai());
//        lichSuHoaDonRepository.save(ls);

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

//        LichSuHoaDon ls = new LichSuHoaDon();
//        ls.setIdHoaDon(hoaDon);
//        ls.setHanhDong("Chuyển trạng thái");
//        ls.setMoTa(request.getMoTaLichSu());
//        ls.setNguoiTao(request.getNguoiTao());
//        ls.setTrangThai(hoaDon.getTrangThai());
//        lichSuHoaDonRepository.save(ls);

        return new ResponseObject<>(
                hd1,
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

}
