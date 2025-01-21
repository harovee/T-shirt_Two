package com.shop.server.core.admin.billhistory.service.impl;

import com.shop.server.core.admin.bill.repository.AdminBillRepository;
import com.shop.server.core.admin.billhistory.model.request.AdminCreateBillHistoryRequest;
import com.shop.server.core.admin.billhistory.model.request.AdminCreateHistoryRequest;
import com.shop.server.core.admin.billhistory.model.request.AdminFindBillHistoryRequest;
import com.shop.server.core.admin.billhistory.repository.AdminBillHistoryRepository;
import com.shop.server.core.admin.billhistory.service.AdminBillHistoryService;
import com.shop.server.core.common.base.PageableObject;
import com.shop.server.core.common.base.ResponseObject;
import com.shop.server.entities.main.LichSuHoaDon;
import com.shop.server.infrastructure.constants.module.Message;
import com.shop.server.utils.Helper;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class AdminBillHistoryServiceImpl implements AdminBillHistoryService {
    private final AdminBillHistoryRepository adminBillHistoryRepository;

    private final AdminBillRepository adminBillRepository;

    public AdminBillHistoryServiceImpl(AdminBillHistoryRepository adminBillHistoryRepository, AdminBillRepository adminBillRepository) {
        this.adminBillHistoryRepository = adminBillHistoryRepository;
        this.adminBillRepository = adminBillRepository;
    }

    @Override
    public ResponseObject<?> getAdminBillHistory(AdminFindBillHistoryRequest request) {
        Pageable pageable = Helper.createPageable(request);
        return new ResponseObject<>(
                PageableObject.of(adminBillHistoryRepository.getAdminBillHistoryByRequest(pageable, request)),
                HttpStatus.OK,
                Message.Success.GET_SUCCESS
        );
    }


    @Override
    public ResponseObject<?> getAdminBillHistoryById(String id) {
        return new ResponseObject<>(
                adminBillHistoryRepository.getAdminBillHistoryById(id),
                HttpStatus.OK,
                Message.Success.GET_SUCCESS
        );
    }

    @Override
    public ResponseObject<?> createAdminBillHistory(AdminCreateHistoryRequest request) {
        LichSuHoaDon lshd = new LichSuHoaDon();
        lshd.setIdHoaDon(request.getIdHoaDon() != null ? adminBillRepository.findById(request.getIdHoaDon()).orElse(null) : null);
        lshd.setMoTa(request.getMoTa());
        lshd.setHanhDong(request.getHanhDong());
        lshd.setTrangThai(request.getTrangThai());
        lshd.setDeleted(false);
        LichSuHoaDon lshd1 = adminBillHistoryRepository.save(lshd);
        return new ResponseObject<>(lshd1, HttpStatus.CREATED, "Tạo lịch sử hóa đơn thành công.");
    }
}
