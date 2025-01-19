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

import java.util.Random;

@Service
public class AdminBillServiceImpl implements AdminBillService {
    private final AdminBillRepository adminBillRepository;
    private final KhachHangRepository khachHangRepository;
    private final NhanVienRepository nhanVienRepository;
    private final PhieuGiamGiaRepository phieuGiamGiaRepository;
    private final LichSuHoaDonRepository lichSuHoaDonRepository;

    public AdminBillServiceImpl(AdminBillRepository adminBillRepository, KhachHangRepository khachHangRepository, NhanVienRepository nhanVienRepository, PhieuGiamGiaRepository phieuGiamGiaRepository, LichSuHoaDonRepository lichSuHoaDonRepository) {
        this.adminBillRepository = adminBillRepository;
        this.khachHangRepository = khachHangRepository;
        this.nhanVienRepository = nhanVienRepository;
        this.phieuGiamGiaRepository = phieuGiamGiaRepository;
        this.lichSuHoaDonRepository = lichSuHoaDonRepository;
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
            hoaDon.setTrangThai("Tạo đơn hàng");
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

        LichSuHoaDon ls = new LichSuHoaDon();
        ls.setIdHoaDon(hoaDon);
        ls.setHanhDong("Tạo đơn hàng");
        if (hoaDon.getLoaiHD().equalsIgnoreCase("Online")) {
            ls.setMoTa("Hóa đơn tạo bởi " + kh.getFullName());
        } else {
            ls.setMoTa("Hóa đơn tạo bởi " + nv.getFullName());
        }
        ls.setTrangThai(hoaDon.getTrangThai());
        lichSuHoaDonRepository.save(ls);
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
//        hoaDon.setTrangThai();

        if (request.getIdKhachHang() != null) {
            KhachHang khachHang = khachHangRepository.findById(request.getIdKhachHang())
                    .orElseThrow(() -> new RuntimeException("Khách hàng không tồn tại"));
            hoaDon.setKhachHang(khachHang);
        }

        PhieuGiamGia phieuGiamGia = phieuGiamGiaRepository.findById(request.getIdPhieuGiamGia())
                        .orElseThrow(() -> new RuntimeException("Không tồn tại phiếu giảm giá"));
        hoaDon.setPhieuGiamGia(phieuGiamGia);

        hoaDon.setDiaChiNguoiNhan(request.getDiaChiNguoiNhan());
        hoaDon.setTenNguoiNhan(request.getTenNguoiNhan());
        if (request.getSoDienThoai() != null) {
            hoaDon.setSoDienThoai(request.getSoDienThoai());
        }else {
            hoaDon.setSoDienThoai(khachHangRepository.findById(request.getIdKhachHang()).get().getPhoneNumber());
        }
        hoaDon.setGhiChu(request.getGhiChu());
        adminBillRepository.save(hoaDon);

        LichSuHoaDon ls = new LichSuHoaDon();
        ls.setIdHoaDon(hoaDon);
        ls.setHanhDong("Cập nhật hóa đơn");
        ls.setMoTa("Hóa đơn cập nhật bởi " + hoaDon.getNhanVien().getFullName());
        ls.setTrangThai(hoaDon.getTrangThai());
        lichSuHoaDonRepository.save(ls);

        return new ResponseObject<>(
                null,
                HttpStatus.OK,
                Message.Success.UPDATE_SUCCESS
        );
    }
}
