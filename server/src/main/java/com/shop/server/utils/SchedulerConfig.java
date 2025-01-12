package com.shop.server.utils;

import com.shop.server.core.admin.phieugiamgia.services.Impl.AdPhieuGiamGiaServicesImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@EnableScheduling
@Configuration
public class SchedulerConfig {

    @Autowired
    private AdPhieuGiamGiaServicesImpl phieuGiamGiaService;

    @Scheduled(cron = "0 0 0 * * ?") // Chạy vào lúc 0h mỗi ngày
    public void updateTrangThaiPhieuGiamGia() {
        phieuGiamGiaService.updateTrangThai();
    }
}

