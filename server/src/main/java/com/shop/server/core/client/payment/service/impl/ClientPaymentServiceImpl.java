package com.shop.server.core.client.payment.service.impl;

import com.shop.server.core.admin.bill.repository.AdminBillRepository;
import com.shop.server.core.client.payment.model.request.ClientInvoiceDetailRequest;
import com.shop.server.core.client.payment.model.request.ClientPaymentRequest;
import com.shop.server.core.client.payment.repository.ClientPaymentRepository;
import com.shop.server.core.client.payment.service.ClientPaymentService;
import com.shop.server.core.client.vnpay.model.VNPayResponse;
import com.shop.server.core.client.vnpay.service.VNPayService;
import com.shop.server.core.common.base.ResponseObject;
import com.shop.server.entities.main.*;
import com.shop.server.infrastructure.constants.module.Message;
import com.shop.server.repositories.KhachHangRepository;
import com.shop.server.repositories.LichSuHoaDonRepository;
import com.shop.server.repositories.NhanVienRepository;
import com.shop.server.repositories.PhieuGiamGiaRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class ClientPaymentServiceImpl implements ClientPaymentService {

    private final ClientPaymentRepository clientPaymentRepository;
    private final AdminBillRepository adminBillRepository;
    private final KhachHangRepository khachHangRepository;
    private final NhanVienRepository nhanVienRepository;
    private final PhieuGiamGiaRepository phieuGiamGiaRepository;
    private final LichSuHoaDonRepository lichSuHoaDonRepository;
    private final VNPayService paymentService;

    @Override
    public ResponseObject<?> createInvoice(ClientPaymentRequest request) {
        String maHD;
        Random random = new Random();
        do {
            int number = random.nextInt(10000);
            maHD = String.format("HD%04d", number);
        } while (adminBillRepository.existsHoaDonByMa(maHD));

        HoaDon hoaDon = new HoaDon();
        hoaDon.setMa(maHD);
        hoaDon.setLoaiHD("Online");
        hoaDon.setTrangThai("Chờ xác nhận");

        KhachHang kh = request.getIdKhachHang() != null ? khachHangRepository.findById(request.getIdKhachHang())
                .orElseThrow(() -> new RuntimeException("Khách hàng không tồn tại")) : null;
        hoaDon.setKhachHang(kh);

        NhanVien nv = request.getIdNhanVien() != null ? nhanVienRepository.findById(request.getIdNhanVien())
                .orElseThrow(() -> new RuntimeException("Nhân viên không tồn tại")) : null;
        hoaDon.setNhanVien(nv);

        PhieuGiamGia pgg = request.getIdPhieuGiamGia() != null ? phieuGiamGiaRepository.findById(request.getIdPhieuGiamGia())
                .orElseThrow(() -> new RuntimeException("Phiếu giảm giá không tồn tại")) : null;
        hoaDon.setPhieuGiamGia(pgg);

        hoaDon.setDiaChiNguoiNhan(request.getDiaChiNguoiNhan());
        hoaDon.setGhiChu(request.getGhiChu());
        hoaDon.setSoDienThoai(request.getSoDienThoai());
        hoaDon.setTenNguoiNhan(request.getTenNguoiNhan());
        hoaDon.setTienGiam(request.getTienGiam());
        hoaDon.setTienShip(request.getTienShip());
        hoaDon.setTongTien(request.getTongTien());
        hoaDon.setPhuongThucNhan("Giao hàng");

        adminBillRepository.save(hoaDon);
        if (request.getIdPhieuGiamGia() != null) {
            adminBillRepository.updateQuantityVoucher(request.getIdPhieuGiamGia());
        }

        for(ClientInvoiceDetailRequest req : request.getListSanPhamChiTiets()) {
            clientPaymentRepository.saveProductDetailsToInvoice(req, hoaDon.getId(), request.getIdNhanVien());
            clientPaymentRepository.updateSoLuong(req);
        }

        LichSuHoaDon ls = new LichSuHoaDon();
        ls.setIdHoaDon(hoaDon);
        ls.setHanhDong("Tạo hóa đơn");
        ls.setMoTa("Tạo hóa đơn online");
        ls.setNguoiTao(request.getIdNhanVien());
        ls.setTrangThai(hoaDon.getTrangThai());
        LichSuHoaDon ls1 = lichSuHoaDonRepository.save(ls);

        return new ResponseObject<>(
                null,
                HttpStatus.OK,
                Message.Success.CREATE_SUCCESS
        );
    }

    @Override
    public ResponseObject<?> createInvoiceWithVnPay(ClientPaymentRequest request, HttpServletRequest httpRequest) {
        String maHD;
        Random random = new Random();
        do {
            int number = random.nextInt(10000);
            maHD = String.format("HD%04d", number);
        } while (adminBillRepository.existsHoaDonByMa(maHD));

        HoaDon hoaDon = new HoaDon();
        hoaDon.setMa(maHD);
        hoaDon.setLoaiHD("Online");
        hoaDon.setTrangThai("Chờ xác nhận");

        KhachHang kh = request.getIdKhachHang() != null ? khachHangRepository.findById(request.getIdKhachHang())
                .orElseThrow(() -> new RuntimeException("Khách hàng không tồn tại")) : null;
        hoaDon.setKhachHang(kh);

        NhanVien nv = request.getIdNhanVien() != null ? nhanVienRepository.findById(request.getIdNhanVien())
                .orElseThrow(() -> new RuntimeException("Nhân viên không tồn tại")) : null;
        hoaDon.setNhanVien(nv);

        PhieuGiamGia pgg = request.getIdPhieuGiamGia() != null ? phieuGiamGiaRepository.findById(request.getIdPhieuGiamGia())
                .orElseThrow(() -> new RuntimeException("Phiếu giảm giá không tồn tại")) : null;
        hoaDon.setPhieuGiamGia(pgg);

        hoaDon.setDiaChiNguoiNhan(request.getDiaChiNguoiNhan());
        hoaDon.setGhiChu(request.getGhiChu());
        hoaDon.setSoDienThoai(request.getSoDienThoai());
        hoaDon.setTenNguoiNhan(request.getTenNguoiNhan());
        hoaDon.setTienGiam(request.getTienGiam());
        hoaDon.setTienShip(request.getTienShip());
        hoaDon.setTongTien(request.getTongTien());

        adminBillRepository.save(hoaDon);
        if (request.getIdPhieuGiamGia() != null) {
            adminBillRepository.updateQuantityVoucher(request.getIdPhieuGiamGia());
        }

        for(ClientInvoiceDetailRequest req : request.getListSanPhamChiTiets()) {
            clientPaymentRepository.saveProductDetailsToInvoice(req, hoaDon.getId(), request.getIdNhanVien());
            clientPaymentRepository.updateSoLuong(req);
        }

        LichSuHoaDon ls = new LichSuHoaDon();
        ls.setIdHoaDon(hoaDon);
        ls.setHanhDong("Tạo hóa đơn");
        ls.setMoTa("Tạo hóa đơn online");
        ls.setNguoiTao(request.getIdNhanVien());
        ls.setTrangThai(hoaDon.getTrangThai());
        LichSuHoaDon ls1 = lichSuHoaDonRepository.save(ls);

        if (request.getPaymentMethod().equals("vnpay")) {
            VNPayResponse vnPayResponse = paymentService.createVnPayPayment(httpRequest, hoaDon.getId());
            return new ResponseObject<>(vnPayResponse, HttpStatus.OK, "Redirect to VNPay");
        }

        return new ResponseObject<>(
                null,
                HttpStatus.OK,
                Message.Success.CREATE_SUCCESS
        );
    }
}
