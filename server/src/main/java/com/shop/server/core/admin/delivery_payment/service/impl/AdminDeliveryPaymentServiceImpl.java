package com.shop.server.core.admin.delivery_payment.service.impl;

import com.shop.server.core.admin.delivery_payment.model.request.AdminCreateDeliveryPaymentRequest;
import com.shop.server.core.admin.delivery_payment.repository.AdminDeliveryPaymentRepository;
import com.shop.server.core.admin.delivery_payment.service.AdminDeliveryPaymentService;
import com.shop.server.core.common.base.ResponseObject;
import com.shop.server.entities.main.ChiTietPhuongThucThanhToan;
import com.shop.server.entities.main.HoaDon;
import com.shop.server.entities.main.LichSuHoaDon;
import com.shop.server.entities.main.PhuongThucThanhToan;
import com.shop.server.infrastructure.constants.module.Message;
import com.shop.server.repositories.ChiTietPhuongThucThanhToanRepository;
import com.shop.server.repositories.HoaDonRepository;
import com.shop.server.repositories.LichSuHoaDonRepository;
import com.shop.server.repositories.PhuongThucThanhToanRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AdminDeliveryPaymentServiceImpl implements AdminDeliveryPaymentService {
    private final HoaDonRepository hoaDonRepository;
    private final AdminDeliveryPaymentRepository adminDeliveryPaymentRepository;
    private final PhuongThucThanhToanRepository phuongThucThanhToanRepository;
    private final LichSuHoaDonRepository lichSuHoaDonRepository;

    public AdminDeliveryPaymentServiceImpl(HoaDonRepository hoaDonRepository, AdminDeliveryPaymentRepository adminDeliveryPaymentRepository, PhuongThucThanhToanRepository phuongThucThanhToanRepository, LichSuHoaDonRepository lichSuHoaDonRepository) {
        this.hoaDonRepository = hoaDonRepository;
        this.adminDeliveryPaymentRepository = adminDeliveryPaymentRepository;
        this.phuongThucThanhToanRepository = phuongThucThanhToanRepository;
        this.lichSuHoaDonRepository = lichSuHoaDonRepository;
    }


    @Override
    public ResponseObject<?> createDeliveryPayment(AdminCreateDeliveryPaymentRequest request) {
        Optional<HoaDon> billOpt = hoaDonRepository.findById(request.getIdHoaDon());
        Optional<PhuongThucThanhToan> methodOpt = phuongThucThanhToanRepository.findById(request.getIdPhuongThucThanhToan());

        if (!billOpt.isPresent() && !methodOpt.isPresent()) {
            return ResponseObject.errorForward(
                    HttpStatus.BAD_REQUEST,
                    Message.Error.GET_ERROR
            );
        }

        HoaDon hoaDon = billOpt.get();
        PhuongThucThanhToan phuongThuc = methodOpt.get();
        ChiTietPhuongThucThanhToan cttt = new ChiTietPhuongThucThanhToan();
        cttt.setHoaDon(hoaDon);
        cttt.setPhuongThucThanhToan(phuongThuc);
        cttt.setMaGiaoDich(request.getMaGiaoDich());
        cttt.setTienKhachDua(request.getTienKhachDua());
        cttt.setSoTienDu(request.getSoTienDu());
        cttt.setGhiChu(request.getGhiChu());

        ChiTietPhuongThucThanhToan ct1 = adminDeliveryPaymentRepository.save(cttt);

        hoaDon.setTrangThai("Đã thanh toán");
        HoaDon hd1 = hoaDonRepository.save(hoaDon);

        LichSuHoaDon ls = new LichSuHoaDon();
        ls.setIdHoaDon(hoaDon);
        ls.setHanhDong("Thanh toán thành công, chuyển trạng thái hóa đơn -> 'Đã thanh toán'");
        ls.setMoTa(request.getMoTa());
        ls.setTrangThai(hoaDon.getTrangThai());
        LichSuHoaDon ls1 = lichSuHoaDonRepository.save(ls);

        return ResponseObject.successForward(
                HttpStatus.CREATED,
                Message.Success.CREATE_SUCCESS
        );
    }
}
