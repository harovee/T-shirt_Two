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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public ResponseObject<?> getBillDetailRefundByMaHD(String maHoaDon) {
        return new ResponseObject<>(
                adminBillDetailRepository.getAdminBillDetailByMaHD(maHoaDon),
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
            billDetail.setGia(sanPhamChiTiet.getGia());
            // ✅ Luôn đảm bảo giá trị không bị null
            BigDecimal currentPrice = billDetail.getGia() != null ? billDetail.getGia() : BigDecimal.ZERO;

            // ✅ Cập nhật số lượng
            int oldQuantity = billDetail.getSoLuong();
            int newQuantity = oldQuantity + request.getSoLuong();
            billDetail.setSoLuong((short) newQuantity);
            // ✅ Tính lại thành tiền
            BigDecimal newTotalAmount = currentPrice.multiply(new BigDecimal(newQuantity));
            billDetail.setThanhTien(newTotalAmount);
            request.setIdHoaDonChiTiet(billDetail.getId());
            adminBillDetailRepository.decreaseStockInAdd(sanPhamChiTiet.getId(), request.getSoLuong());
        } else {
            billDetail = new HoaDonChiTiet();
            billDetail.setHoaDon(hoaDon);
            billDetail.setSanPhamChiTiet(sanPhamChiTiet);
            billDetail.setGia(sanPhamChiTiet.getGia());
            billDetail.setSoLuong(request.getSoLuong());
            // ✅ Kiểm tra giá trước khi nhân
            BigDecimal currentPrice = sanPhamChiTiet.getGia() != null ? sanPhamChiTiet.getGia() : BigDecimal.ZERO;
            BigDecimal totalAmount = currentPrice.multiply(new BigDecimal(request.getSoLuong()));

            billDetail.setGia(currentPrice); // Gán giá trị để tránh `null`
            billDetail.setThanhTien(totalAmount);
            adminBillDetailRepository.decreaseStockInAdd(sanPhamChiTiet.getId(), request.getSoLuong());
        }
        adminBillDetailRepository.save(billDetail);

        // ✅ Cập nhật tổng tiền hóa đơn
        updateBillTotalAmount(hoaDon);

        return ResponseObject.successForward(
                HttpStatus.CREATED,
                Message.Success.CREATE_SUCCESS
        );
    }

    // đang dùng
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
        HoaDon hoaDon = billDetail.getHoaDon();

        if (request.getSoLuong() <= 0) {
            // Xóa chi tiết hóa đơn nếu số lượng <= 0
            adminBillDetailRepository.delete(billDetail);
        } else {
            // Cập nhật chi tiết hóa đơn
            if (hoaDon.getLoaiHD().equals("Tại quầy")) {
                adminBillDetailRepository.updateQuantityProductDetailInBill(request);
            }
            BigDecimal currentPrice = billDetail.getGia();
            BigDecimal newAmount = currentPrice.multiply(new BigDecimal(request.getSoLuong()));
            billDetail.setSoLuong(request.getSoLuong());
            billDetail.setThanhTien(newAmount);
            adminBillDetailRepository.save(billDetail);
        }

        return ResponseObject.successForward(
                HttpStatus.OK,
                Message.Success.UPDATE_SUCCESS
        );
    }


    private void updateBillTotalAmount(HoaDon hoaDon) {
        // Tìm tất cả các chi tiết hóa đơn liên quan đến hóa đơn
        List<HoaDonChiTiet> billDetails = adminBillDetailRepository.findAll()
                .stream()
                .filter(detail -> detail.getHoaDon().equals(hoaDon))
                .collect(Collectors.toList());

        // Tính tổng tiền dựa trên thành tiền
        BigDecimal totalAmount = billDetails.stream()
                .map(HoaDonChiTiet::getThanhTien)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Cập nhật tổng tiền của hóa đơn
        hoaDon.setTongTien(totalAmount);
        hoaDonRepository.save(hoaDon);
    }


}
