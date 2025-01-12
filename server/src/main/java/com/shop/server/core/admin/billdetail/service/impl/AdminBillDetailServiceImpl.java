package com.shop.server.core.admin.billdetail.service.impl;

import com.shop.server.core.admin.bill.repository.AdminBillRepository;
import com.shop.server.core.admin.billdetail.model.request.AdminCreateBillDetailRequest;
import com.shop.server.core.admin.billdetail.model.request.AdminFindBillDetailRequest;
import com.shop.server.core.admin.billdetail.model.request.AdminUpdateBillDetailRequest;
import com.shop.server.core.admin.billdetail.repository.AdminBillDetailRepository;
import com.shop.server.core.admin.billdetail.service.AdminBillDetailService;
import com.shop.server.core.common.base.PageableObject;
import com.shop.server.core.common.base.ResponseObject;
import com.shop.server.entities.main.HoaDon;
import com.shop.server.entities.main.HoaDonChiTiet;
import com.shop.server.entities.main.SanPhamChiTiet;
import com.shop.server.infrastructure.constants.module.Message;
import com.shop.server.repositories.HoaDonRepository;
import com.shop.server.repositories.SanPhamChiTietRepository;
import com.shop.server.utils.Helper;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class AdminBillDetailServiceImpl implements AdminBillDetailService {
    private final AdminBillDetailRepository adminBillDetailRepository;
    private final HoaDonRepository hoaDonRepository;
    private final SanPhamChiTietRepository sanPhamChiTietRepository;

    public AdminBillDetailServiceImpl(AdminBillDetailRepository adminBillDetailRepository, HoaDonRepository hoaDonRepository, SanPhamChiTietRepository sanPhamChiTietRepository, AdminBillRepository adminBillRepository) {
        this.adminBillDetailRepository = adminBillDetailRepository;
        this.hoaDonRepository = hoaDonRepository;
        this.sanPhamChiTietRepository = sanPhamChiTietRepository;
    }

    @Override
    public ResponseObject<?> getBillDetailsByRequest(AdminFindBillDetailRequest request) {
        Pageable pageable = Helper.createPageable(request);
        return new ResponseObject<>(
                PageableObject.of(adminBillDetailRepository.getAdminBillDetailByRequest(pageable, request)),
                HttpStatus.OK,
                Message.Success.GET_SUCCESS
        );
    }


    @Override
    public ResponseObject<?> getBillDetailById(String id) {
        return new ResponseObject<>(
                adminBillDetailRepository.getAdminBillDetailById(id),
                HttpStatus.OK,
                Message.Success.GET_SUCCESS
        );
    }

    @Override
    public ResponseObject<?> createBillDetail(AdminCreateBillDetailRequest request) {
        Optional<HoaDon> billOpt = hoaDonRepository.findById(request.getIdHoaDon());
        Optional<SanPhamChiTiet> prodOpt = sanPhamChiTietRepository.findById(request.getIdSanPhamChiTiet());

        if (!billOpt.isPresent() || !prodOpt.isPresent()) {
            return ResponseObject.errorForward(
                    HttpStatus.BAD_REQUEST,
                    Message.Error.GET_ERROR
            );
        }

        HoaDon hoaDon = billOpt.get();
        SanPhamChiTiet sanPhamChiTiet = prodOpt.get();

        Optional<HoaDonChiTiet> existingBillDetailOpt = adminBillDetailRepository.findByHoaDonAndSanPhamChiTiet(hoaDon, sanPhamChiTiet);

        HoaDonChiTiet billDetail;
        if (existingBillDetailOpt.isPresent()) {
            billDetail = existingBillDetailOpt.get();
            billDetail.setSoLuong((short) (billDetail.getSoLuong() + request.getSoLuong()));
            BigDecimal currentPrice = billDetail.getGia();
            BigDecimal quantity = new BigDecimal(request.getSoLuong());
            billDetail.setThanhTien(billDetail.getThanhTien().add(currentPrice.multiply(quantity)));
        } else {
            billDetail = new HoaDonChiTiet();
            billDetail.setHoaDon(hoaDon);
            billDetail.setSanPhamChiTiet(sanPhamChiTiet);
            billDetail.setSoLuong(request.getSoLuong());

            BigDecimal currentPrice = sanPhamChiTiet.getGia();
            BigDecimal quantity = new BigDecimal(request.getSoLuong());
            billDetail.setThanhTien(currentPrice.multiply(quantity));
        }

        adminBillDetailRepository.save(billDetail);

        return ResponseObject.successForward(
                HttpStatus.CREATED,
                Message.Success.CREATE_SUCCESS
        );
    }


    @Override
    public ResponseObject<?> updateBillDetail(String id, AdminUpdateBillDetailRequest request) {
        Optional<HoaDonChiTiet> billDetailOpt = adminBillDetailRepository.findById(id);

        if (!billDetailOpt.isPresent()) {
            return ResponseObject.errorForward(
                    HttpStatus.BAD_REQUEST,
                    Message.Error.GET_ERROR
            );
        }

        HoaDonChiTiet billDetail = billDetailOpt.get();

        if (request.getSoLuong() <= 0) {
            adminBillDetailRepository.delete(billDetail);
        } else {
            billDetail.setSoLuong(request.getSoLuong());
            BigDecimal currentPrice = billDetail.getGia();
            BigDecimal quantity = new BigDecimal(billDetail.getSoLuong());
            billDetail.setThanhTien(currentPrice.multiply(quantity));
            adminBillDetailRepository.save(billDetail);
        }

        return ResponseObject.successForward(
                HttpStatus.OK,
                Message.Success.UPDATE_SUCCESS
        );
    }

}
