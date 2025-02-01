package com.shop.server.utils;

import com.shop.server.core.admin.phieugiamgia.services.Impl.AdPhieuGiamGiaServicesImpl;
import com.shop.server.entities.main.PhieuGiamGia;
import com.shop.server.repositories.PhieuGiamGiaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDate;
import java.util.List;

@EnableScheduling
@Configuration
public class SchedulerConfig {

    private final PhieuGiamGiaRepository phieuGiamGiaRepository;

    public SchedulerConfig(PhieuGiamGiaRepository phieuGiamGiaRepository) {
        this.phieuGiamGiaRepository = phieuGiamGiaRepository;
    }

    // Cập nhật trạng thái dựa trên ngày bắt đầu và ngày kết thúc
    public void capNhatTrangThai(PhieuGiamGia phieuGiamGia) {
        LocalDate now = LocalDate.now();
        LocalDate ngayBatDau = LocalDate.ofEpochDay(phieuGiamGia.getNgayBatDau());
        LocalDate ngayKetThuc = LocalDate.ofEpochDay(phieuGiamGia.getNgayKetThuc());
        if (now.isBefore(ngayBatDau)) {
            phieuGiamGia.setTrangThai("NOT_STARTED");
        } else if (now.isAfter(ngayKetThuc)) {
            phieuGiamGia.setTrangThai("EXPRIED");
        } else {
            phieuGiamGia.setTrangThai("ACTIVE");
        }
    }

    // Gọi khi thêm mới
    public PhieuGiamGia save(PhieuGiamGia phieuGiamGia) {
        capNhatTrangThai(phieuGiamGia);
        return phieuGiamGiaRepository.save(phieuGiamGia);
    }

    // Gọi khi cập nhật
    public PhieuGiamGia update(PhieuGiamGia phieuGiamGia) {
        capNhatTrangThai(phieuGiamGia);
        return phieuGiamGiaRepository.save(phieuGiamGia);
    }

    // Lịch trình kiểm tra hàng ngày
    @Scheduled(cron = "0 0 0 * * ?") // Mỗi ngày lúc 00:00
    public void capNhatTrangThaiHangNgay() {
        List<PhieuGiamGia> danhSachPhieu = phieuGiamGiaRepository.findAll();
        for (PhieuGiamGia phieu : danhSachPhieu) {
            capNhatTrangThai(phieu);
            phieuGiamGiaRepository.save(phieu);
        }
    }
}

