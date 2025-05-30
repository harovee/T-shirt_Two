package com.shop.server.core.client.payment.service.impl;

import com.shop.server.core.admin.ban_hang.repository.AdminChiTietPhuongThucThanhToanRepository;
import com.shop.server.core.admin.ban_hang.repository.AdminHoaDonRepository;
import com.shop.server.core.admin.bill.model.request.AdminSendEmailRequest;
import com.shop.server.core.admin.bill.repository.AdminBillRepository;
import com.shop.server.core.admin.bill.service.AdminBillSendMailService;
import com.shop.server.core.admin.billdetail.repository.AdminBillDetailRepository;
import com.shop.server.core.client.momo.model.request.ClientMomoRequest;
import com.shop.server.core.client.momo.services.Impl.MomoServices;
import com.shop.server.core.client.payment.model.request.ClientInvoiceDetailRequest;
import com.shop.server.core.client.payment.model.request.ClientPaymentRequest;
import com.shop.server.core.client.payment.repository.ClientPaymentRepository;
import com.shop.server.core.client.payment.repository.ClientPhuongThucThanhToanRepository;
import com.shop.server.core.client.payment.service.ClientPaymentService;
import com.shop.server.core.client.vnpay.model.VNPayRequest;
import com.shop.server.core.client.vnpay.model.VNPayResponse;
import com.shop.server.core.client.vnpay.service.VNPayService;
import com.shop.server.core.common.base.ResponseObject;
import com.shop.server.entities.main.*;
import com.shop.server.infrastructure.constants.module.Message;
import com.shop.server.repositories.*;
import com.shop.server.utils.VNPayUtil;
import com.shop.websocket.model.entity.OrderNotification;
import com.shop.websocket.repository.NotificationRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class ClientPaymentServiceImpl implements ClientPaymentService {

    private final ClientPaymentRepository clientPaymentRepository;
    private final AdminBillRepository adminBillRepository;
    private final KhachHangRepository khachHangRepository;
    private final NhanVienRepository nhanVienRepository;
    private final PhieuGiamGiaRepository phieuGiamGiaRepository;
    private final LichSuHoaDonRepository lichSuHoaDonRepository;
    private final VNPayService paymentService;
    private final AdminHoaDonRepository hoaDonRepository;
    private final AdminChiTietPhuongThucThanhToanRepository chiTietPhuongThucThanhToanRepository;
    private final ClientPhuongThucThanhToanRepository phuongThucThanhToanRepository;
    private final AdminBillDetailRepository hoaDonChiTietRepository;
    private final SanPhamChiTietRepository sanPhamChiTietRepository;
    private final AdminBillSendMailService adminBillSendMailService;
    private final MomoServices momoService;
    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

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
        hoaDon.setTinh(request.getTinh());
        hoaDon.setXa(request.getXa());
        hoaDon.setHuyen(request.getHuyen());
        hoaDon.setEmailNguoiNhan(request.getEmail());

        adminBillRepository.save(hoaDon);
        AdminSendEmailRequest adminSendEmailRequest = new AdminSendEmailRequest();
        adminSendEmailRequest.setEmail(request.getEmail());
        adminSendEmailRequest.setMaHoaDon(hoaDon.getMa());
        adminSendEmailRequest.setTrangThai("Chờ xác nhận");
        adminSendEmailRequest.setGhiChu(request.getGhiChu() == null ? "" : request.getGhiChu());
        adminSendEmailRequest.setIdHoaDon(hoaDon.getId());
        adminBillSendMailService.sendMailCreateBill(adminSendEmailRequest);
        if (request.getIdPhieuGiamGia() != null) {
            adminBillRepository.updateQuantityVoucher(request.getIdPhieuGiamGia());
        }

        for(ClientInvoiceDetailRequest req : request.getListSanPhamChiTiets()) {
            clientPaymentRepository.saveProductDetailsToInvoice(req, hoaDon.getId(), request.getIdNhanVien());
//            clientPaymentRepository.updateSoLuong(req);
        }

        // lưu thông báo - gửi thông báo
        OrderNotification orderNotification = new OrderNotification();
        orderNotification.setOrderId(hoaDon.getId());
        orderNotification.setContent("Hóa đơn " + hoaDon.getMa() + " cần xác nhận");
        OrderNotification noti  = notificationRepository.save(orderNotification);
        messagingTemplate.convertAndSend("/topic/notification", noti);

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
    @Transactional
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
        hoaDon.setPhuongThucNhan("Giao hàng");
        hoaDon.setEmailNguoiNhan(request.getEmail());


        adminBillRepository.save(hoaDon);
        AdminSendEmailRequest adminSendEmailRequest = new AdminSendEmailRequest();
        adminSendEmailRequest.setEmail(request.getEmail());
        adminSendEmailRequest.setMaHoaDon(hoaDon.getMa());
        adminSendEmailRequest.setTrangThai("Chờ xác nhận");
        adminSendEmailRequest.setGhiChu(request.getGhiChu() == null ? "" : request.getGhiChu());
        adminSendEmailRequest.setIdHoaDon(hoaDon.getId());
        adminBillSendMailService.sendMailCreateBill(adminSendEmailRequest);

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
            VNPayRequest vnPayRequest = new VNPayRequest();
            vnPayRequest.setAmount(String.valueOf(request.getTongTien()));
            vnPayRequest.setBankCode(request.getBankCode());
            VNPayResponse vnPayResponse = paymentService.createVnPayPayment(vnPayRequest, VNPayUtil.getIpAddress(httpRequest), hoaDon.getId());
            return new ResponseObject<>(vnPayResponse, HttpStatus.OK, "Redirect to VNPay");
        }

        return new ResponseObject<>(
                null,
                HttpStatus.OK,
                Message.Success.CREATE_SUCCESS
        );
    }

    @Override
    @Transactional
    public ResponseObject<?> createInvoiceWithMomo(ClientPaymentRequest request) {
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
        hoaDon.setPhuongThucNhan("Giao hàng");
        hoaDon.setTongTien(request.getTongTien());

        adminBillRepository.save(hoaDon);

        adminBillRepository.save(hoaDon);
        AdminSendEmailRequest adminSendEmailRequest = new AdminSendEmailRequest();
        adminSendEmailRequest.setEmail(request.getEmail());
        adminSendEmailRequest.setMaHoaDon(hoaDon.getMa());
        adminSendEmailRequest.setTrangThai("Chờ xác nhận");
        adminSendEmailRequest.setGhiChu(request.getGhiChu() == null ? "" : request.getGhiChu());
        adminSendEmailRequest.setIdHoaDon(hoaDon.getId());
        adminBillSendMailService.sendMailCreateBill(adminSendEmailRequest);
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
        if (request.getPaymentMethod().equals("momo")){
            ClientMomoRequest momoRequest = new ClientMomoRequest();
            momoRequest.setAmount(request.getTongTien().longValue());
            momoRequest.setOrderId(hoaDon.getId());
            return new ResponseObject<>(momoService.createMomo(momoRequest),HttpStatus.OK, "Redirect to Momo");
        }
        return new ResponseObject<>(
                null,
                HttpStatus.OK,
                Message.Success.CREATE_SUCCESS
        );
    }

    @Override
    public ResponseObject<?> createInvoiceWithVietQr(ClientPaymentRequest request) {
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
        hoaDon.setTinh(request.getTinh());
        hoaDon.setXa(request.getXa());
        hoaDon.setHuyen(request.getHuyen());
        hoaDon.setEmailNguoiNhan(request.getEmail());

        adminBillRepository.save(hoaDon);
        AdminSendEmailRequest adminSendEmailRequest = new AdminSendEmailRequest();
        adminSendEmailRequest.setEmail(request.getEmail());
        adminSendEmailRequest.setMaHoaDon(hoaDon.getMa());
        adminSendEmailRequest.setTrangThai("Chờ xác nhận");
        adminSendEmailRequest.setGhiChu(request.getGhiChu() == null ? "" : request.getGhiChu());
        adminSendEmailRequest.setIdHoaDon(hoaDon.getId());
        adminBillSendMailService.sendMailCreateBill(adminSendEmailRequest);
        if (request.getIdPhieuGiamGia() != null) {
            adminBillRepository.updateQuantityVoucher(request.getIdPhieuGiamGia());
        }

        for(ClientInvoiceDetailRequest req : request.getListSanPhamChiTiets()) {
            clientPaymentRepository.saveProductDetailsToInvoice(req, hoaDon.getId(), request.getIdNhanVien());
//            clientPaymentRepository.updateSoLuong(req);
        }

        // lưu thông báo - gửi thông báo
        OrderNotification orderNotification = new OrderNotification();
        orderNotification.setOrderId(hoaDon.getId());
        orderNotification.setContent("Hóa đơn " + hoaDon.getMa() + " cần xác nhận");
        OrderNotification noti  = notificationRepository.save(orderNotification);
        messagingTemplate.convertAndSend("/topic/notification", noti);

        LichSuHoaDon ls = new LichSuHoaDon();
        ls.setIdHoaDon(hoaDon);
        ls.setHanhDong("Tạo hóa đơn");
        ls.setMoTa("Tạo hóa đơn online");
        ls.setNguoiTao(request.getIdNhanVien());
        ls.setTrangThai(hoaDon.getTrangThai());
        LichSuHoaDon ls1 = lichSuHoaDonRepository.save(ls);
        ChiTietPhuongThucThanhToan chiTietPTTT = new ChiTietPhuongThucThanhToan();
        chiTietPTTT.setHoaDon(hoaDon);
        chiTietPTTT.setMaGiaoDich(request.getMaGiaoDich());
        chiTietPTTT.setTienKhachDua(request.getTongTien());
        chiTietPTTT.setGhiChu("Thanh toán qua VietQr");
        PhuongThucThanhToan pttt = phuongThucThanhToanRepository.findPhuongThucThanhToanByName("Chuyển khoản");
        chiTietPTTT.setPhuongThucThanhToan(pttt);
        chiTietPhuongThucThanhToanRepository.save(chiTietPTTT);
        return new ResponseObject<>(
                null,
                HttpStatus.OK,
                Message.Success.CREATE_SUCCESS
        );
    }

    @Transactional
    public void handleVnPaySuccess(Map<String, String> params, String invoiceId) {
        try {
            HoaDon hoaDon = hoaDonRepository.findById(invoiceId)
                    .orElseThrow(() -> new RuntimeException("Invoice not found with ID: " + invoiceId));

            String transactionNo = params.get("vnp_TransactionNo");
            String amount = params.get("vnp_Amount");
            BigDecimal paymentAmount = new BigDecimal(amount);

            ChiTietPhuongThucThanhToan chiTietPTTT = new ChiTietPhuongThucThanhToan();
            chiTietPTTT.setHoaDon(hoaDon);
            chiTietPTTT.setMaGiaoDich(transactionNo);
            chiTietPTTT.setTienKhachDua(paymentAmount.divide(new BigDecimal(100)));
            chiTietPTTT.setGhiChu("VNPay Payment - Transaction");
            PhuongThucThanhToan pttt = phuongThucThanhToanRepository.findPhuongThucThanhToanByName("Chuyển khoản");
            chiTietPTTT.setPhuongThucThanhToan(pttt);
            chiTietPhuongThucThanhToanRepository.save(chiTietPTTT);
            log.info("VNPay payment processed successfully: Invoice {}, Amount {}, Transaction {}",
                    invoiceId, paymentAmount, transactionNo);
        } catch (Exception e) {
            log.error("Error processing VNPay payment: " + e.getMessage(), e);
            throw new RuntimeException("Failed to process payment", e);
        }
    }

    @Transactional
    public ResponseObject<?> handlePayMentVnPaySuccess(ClientPaymentRequest request) {
        try {
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
            hoaDon.setTinh(request.getTinh());
            hoaDon.setXa(request.getXa());
            hoaDon.setHuyen(request.getHuyen());
            hoaDon.setEmailNguoiNhan(request.getEmail());

            adminBillRepository.save(hoaDon);
            AdminSendEmailRequest adminSendEmailRequest = new AdminSendEmailRequest();
            adminSendEmailRequest.setEmail(request.getEmail());
            adminSendEmailRequest.setMaHoaDon(hoaDon.getMa());
            adminSendEmailRequest.setTrangThai("Chờ xác nhận");
            adminSendEmailRequest.setGhiChu(request.getGhiChu() == null ? "" : request.getGhiChu());
            adminSendEmailRequest.setIdHoaDon(hoaDon.getId());
            adminBillSendMailService.sendMailCreateBill(adminSendEmailRequest);
            if (request.getIdPhieuGiamGia() != null) {
                adminBillRepository.updateQuantityVoucher(request.getIdPhieuGiamGia());
            }

            for(ClientInvoiceDetailRequest req : request.getListSanPhamChiTiets()) {
                clientPaymentRepository.saveProductDetailsToInvoice(req, hoaDon.getId(), request.getIdNhanVien());
//            clientPaymentRepository.updateSoLuong(req);
            }

            // lưu thông báo - gửi thông báo
            OrderNotification orderNotification = new OrderNotification();
            orderNotification.setOrderId(hoaDon.getId());
            orderNotification.setContent("Hóa đơn " + hoaDon.getMa() + " cần xác nhận");
            OrderNotification noti  = notificationRepository.save(orderNotification);
            messagingTemplate.convertAndSend("/topic/notification", noti);

            LichSuHoaDon ls = new LichSuHoaDon();
            ls.setIdHoaDon(hoaDon);
            ls.setHanhDong("Tạo hóa đơn");
            ls.setMoTa("Tạo hóa đơn online");
            ls.setNguoiTao(request.getIdNhanVien());
            ls.setTrangThai(hoaDon.getTrangThai());
            LichSuHoaDon ls1 = lichSuHoaDonRepository.save(ls);
            ChiTietPhuongThucThanhToan chiTietPTTT = new ChiTietPhuongThucThanhToan();
            chiTietPTTT.setHoaDon(hoaDon);
            chiTietPTTT.setMaGiaoDich(request.getMaGiaoDich());
            chiTietPTTT.setTienKhachDua(request.getTongTien());
            chiTietPTTT.setGhiChu("VNPay Payment - Transaction");
            PhuongThucThanhToan pttt = phuongThucThanhToanRepository.findPhuongThucThanhToanByName("Chuyển khoản");
            chiTietPTTT.setPhuongThucThanhToan(pttt);
            chiTietPhuongThucThanhToanRepository.save(chiTietPTTT);
            log.info("VNPay payment processed successfully: Invoice {}, Amount {}, Transaction {}",hoaDon.getId());
            return new ResponseObject<>(
                    null,
                    HttpStatus.OK,
                    Message.Success.CREATE_SUCCESS
            );
        } catch (Exception e) {
            log.error("Error processing VNPay payment: " + e.getMessage(), e);
            throw new RuntimeException("Failed to process payment", e);
        }
    }

    @Transactional
    public void handleMomoSuccess(Map<String, String> params, String invoiceId) {
        try {
            HoaDon hoaDon = hoaDonRepository.findById(invoiceId)
                    .orElseThrow(() -> new RuntimeException("Invoice not found with ID: " + invoiceId));
            String orderId = params.get("orderId");
            String amount = params.get("amount");
            String transId = params.get("transId");
            String resultCode = params.get("resultCode");
            String message = params.get("message");
            if ("0".equals(resultCode)) {
                BigDecimal paymentAmount = new BigDecimal(amount);
                ChiTietPhuongThucThanhToan chiTietPTTT = new ChiTietPhuongThucThanhToan();
                chiTietPTTT.setHoaDon(hoaDon);
                chiTietPTTT.setMaGiaoDich(transId);
                chiTietPTTT.setTienKhachDua(paymentAmount);
                chiTietPTTT.setGhiChu("Momo Payment - Thanh toán qua MoMo");

                PhuongThucThanhToan pttt = phuongThucThanhToanRepository.findPhuongThucThanhToanByName("Chuyển khoản");
                chiTietPTTT.setPhuongThucThanhToan(pttt);
                chiTietPhuongThucThanhToanRepository.save(chiTietPTTT);
                log.info("Momo payment processed successfully: Invoice {}, Amount {}, Transaction {}",
                        invoiceId, paymentAmount, transId);
            } else {
                log.error("Momo payment failed for Invoice {}: {}", invoiceId, message);
                throw new RuntimeException("Momo payment failed: " + message);
            }

        } catch (Exception e) {
            log.error("Error processing Momo payment for Invoice {}: {}", invoiceId, e.getMessage(), e);
            throw new RuntimeException("Failed to process Momo payment", e);
        }
    }

    @Transactional
    public ResponseObject<?> handlePayMentMomoSuccess(ClientPaymentRequest request) {
        try {
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
            hoaDon.setTinh(request.getTinh());
            hoaDon.setXa(request.getXa());
            hoaDon.setHuyen(request.getHuyen());
            hoaDon.setEmailNguoiNhan(request.getEmail());

            adminBillRepository.save(hoaDon);
            AdminSendEmailRequest adminSendEmailRequest = new AdminSendEmailRequest();
            adminSendEmailRequest.setEmail(request.getEmail());
            adminSendEmailRequest.setMaHoaDon(hoaDon.getMa());
            adminSendEmailRequest.setTrangThai("Chờ xác nhận");
            adminSendEmailRequest.setGhiChu(request.getGhiChu() == null ? "" : request.getGhiChu());
            adminSendEmailRequest.setIdHoaDon(hoaDon.getId());
            adminBillSendMailService.sendMailCreateBill(adminSendEmailRequest);
            if (request.getIdPhieuGiamGia() != null) {
                adminBillRepository.updateQuantityVoucher(request.getIdPhieuGiamGia());
            }

            for(ClientInvoiceDetailRequest req : request.getListSanPhamChiTiets()) {
                clientPaymentRepository.saveProductDetailsToInvoice(req, hoaDon.getId(), request.getIdNhanVien());
//            clientPaymentRepository.updateSoLuong(req);
            }

            // lưu thông báo - gửi thông báo
            OrderNotification orderNotification = new OrderNotification();
            orderNotification.setOrderId(hoaDon.getId());
            orderNotification.setContent("Hóa đơn " + hoaDon.getMa() + " cần xác nhận");
            OrderNotification noti  = notificationRepository.save(orderNotification);
            messagingTemplate.convertAndSend("/topic/notification", noti);

            LichSuHoaDon ls = new LichSuHoaDon();
            ls.setIdHoaDon(hoaDon);
            ls.setHanhDong("Tạo hóa đơn");
            ls.setMoTa("Tạo hóa đơn online");
            ls.setNguoiTao(request.getIdNhanVien());
            ls.setTrangThai(hoaDon.getTrangThai());
            LichSuHoaDon ls1 = lichSuHoaDonRepository.save(ls);
            ChiTietPhuongThucThanhToan chiTietPTTT = new ChiTietPhuongThucThanhToan();
            chiTietPTTT.setHoaDon(hoaDon);
            chiTietPTTT.setMaGiaoDich(request.getMaGiaoDich());
            chiTietPTTT.setTienKhachDua(request.getTongTien());
            chiTietPTTT.setGhiChu("Momo Payment - Thanh toán qua MoMo");
            PhuongThucThanhToan pttt = phuongThucThanhToanRepository.findPhuongThucThanhToanByName("Chuyển khoản");
            chiTietPTTT.setPhuongThucThanhToan(pttt);
            chiTietPhuongThucThanhToanRepository.save(chiTietPTTT);
            return new ResponseObject<>(null,
                    HttpStatus.OK,
                    Message.Success.CREATE_SUCCESS);
        } catch (Exception e) {
            log.error("Error processing Momo payment for Invoice {}: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to process Momo payment", e);
        }
    }

    @Transactional
    public void rollbackInvoiceOnVnPayFailure(String invoiceId, Map<String, String> vnpayResponse) {
        try {
            log.info("Bắt đầu rollback hóa đơn khi thanh toán VNPay thất bại: {}", invoiceId);

            HoaDon hoaDon = adminBillRepository.findById(invoiceId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn với ID: " + invoiceId));
            String responseCode = vnpayResponse.get("vnp_ResponseCode");
            String responseMessage = getVnPayErrorMessage(responseCode);

            List<HoaDonChiTiet> chiTietHoaDons = hoaDonChiTietRepository.findByHoaDon((hoaDon));
            for (HoaDonChiTiet chiTiet : chiTietHoaDons) {
                SanPhamChiTiet spct = chiTiet.getSanPhamChiTiet();
                if (spct != null) {
                    spct.setSoLuong(spct.getSoLuong() + chiTiet.getSoLuong());
                    sanPhamChiTietRepository.save(spct);
                }
                hoaDonChiTietRepository.delete(chiTiet);
            }

            if (hoaDon.getPhieuGiamGia() != null) {
                PhieuGiamGia phieuGiamGia = hoaDon.getPhieuGiamGia();
                phieuGiamGia.setSoLuong((short) (phieuGiamGia.getSoLuong() + 1));
                phieuGiamGiaRepository.save(phieuGiamGia);
            }

            List<LichSuHoaDon> lichSuHoaDons = lichSuHoaDonRepository.findByIdHoaDon(hoaDon);
            lichSuHoaDonRepository.deleteAll(lichSuHoaDons);

            adminBillRepository.delete(hoaDon);
            log.info("Rollback hóa đơn thành công: {}, Mã phản hồi VNPay: {}", invoiceId, responseCode);
        } catch (Exception e) {
            log.error("Lỗi trong quá trình rollback hóa đơn: " + e.getMessage(), e);
            throw new RuntimeException("Không thể rollback hóa đơn. Lỗi: " + e.getMessage(), e);
        }
    }


    @Transactional
    public void rollbackInvoiceOnMomoFailure(String invoiceId, Map<String, String> momoResponse) {
        try {
            log.info("Bắt đầu rollback hóa đơn khi thanh toán Momo thất bại: {}", invoiceId);

            HoaDon hoaDon = adminBillRepository.findById(invoiceId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn với ID: " + invoiceId));

            String resultCode = momoResponse.get("resultCode");
            String message = momoResponse.get("message");
            String responseMessage = "Thanh toán Momo thất bại: " + message;
            List<HoaDonChiTiet> chiTietHoaDons = hoaDonChiTietRepository.findByHoaDon(hoaDon);
            for (HoaDonChiTiet chiTiet : chiTietHoaDons) {
                SanPhamChiTiet spct = chiTiet.getSanPhamChiTiet();
                if (spct != null) {
                    spct.setSoLuong(spct.getSoLuong() + chiTiet.getSoLuong());
                    sanPhamChiTietRepository.save(spct);
                }
                hoaDonChiTietRepository.delete(chiTiet);
            }

            if (hoaDon.getPhieuGiamGia() != null) {
                PhieuGiamGia phieuGiamGia = hoaDon.getPhieuGiamGia();
                phieuGiamGia.setSoLuong((short) (phieuGiamGia.getSoLuong() + 1));
                phieuGiamGiaRepository.save(phieuGiamGia);
            }
            List<LichSuHoaDon> lichSuHoaDons = lichSuHoaDonRepository.findByIdHoaDon(hoaDon);
            lichSuHoaDonRepository.deleteAll(lichSuHoaDons);

//            LichSuHoaDon lichSuHuy = new LichSuHoaDon();
//            lichSuHuy.setIdHoaDon(hoaDon);
//            lichSuHuy.setHanhDong("Hủy hóa đơn");
//            lichSuHuy.setMoTa("Hủy hóa đơn do thanh toán Momo thất bại - Mã lỗi: " + resultCode + " - " + responseMessage);
//            lichSuHuy.setNguoiTao(hoaDon.getNhanVien() != null ? hoaDon.getNhanVien().getId() : null);
//            lichSuHuy.setTrangThai("Đã hủy");
//            lichSuHoaDonRepository.save(lichSuHuy);
            adminBillRepository.delete(hoaDon);
            log.info("Rollback hóa đơn thành công: {}, Mã phản hồi Momo: {}", invoiceId, resultCode);

        } catch (Exception e) {
            log.error("Lỗi trong quá trình rollback hóa đơn: " + e.getMessage(), e);
            throw new RuntimeException("Không thể rollback hóa đơn. Lỗi: " + e.getMessage(), e);
        }
    }

    public String getVnPayErrorMessage(String responseCode) {
        Map<String, String> errorMessages = new HashMap<>();
        errorMessages.put("01", "Giao dịch không thành công");
        errorMessages.put("02", "Lỗi hệ thống");
        errorMessages.put("03", "Dữ liệu giao dịch không hợp lệ");
        errorMessages.put("04", "Số tiền không hợp lệ");
        errorMessages.put("05", "Tài khoản không đủ số dư");
        errorMessages.put("06", "Giao dịch bị hủy bởi người dùng");
        errorMessages.put("07", "Giao dịch bị từ chối bởi ngân hàng");
        errorMessages.put("09", "Thẻ/Tài khoản hết hạn/bị khóa");
        errorMessages.put("10", "Vượt quá hạn mức thanh toán");
        errorMessages.put("11", "Thẻ chưa đăng ký dịch vụ thanh toán trực tuyến");
        errorMessages.put("12", "Ngân hàng đang bảo trì");
        errorMessages.put("13", "Đã hết hạn chờ thanh toán");
        errorMessages.put("24", "Giao dịch không thành công");
        errorMessages.put("51", "Số dư tài khoản không đủ");
        errorMessages.put("65", "Tài khoản đã vượt quá hạn mức giao dịch trong ngày");
        return errorMessages.getOrDefault(responseCode, "Lỗi không xác định với mã: " + responseCode);
    }
}
