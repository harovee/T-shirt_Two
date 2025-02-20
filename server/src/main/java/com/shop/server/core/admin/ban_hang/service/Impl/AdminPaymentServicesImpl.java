package com.shop.server.core.admin.ban_hang.service.Impl;

import com.shop.server.core.admin.ban_hang.model.request.*;
import com.shop.server.core.admin.ban_hang.repository.*;
import com.shop.server.core.admin.ban_hang.service.AdminPaymentServices;
import com.shop.server.core.common.base.PageableObject;
import com.shop.server.core.common.base.ResponseObject;
import com.shop.server.entities.main.ChiTietPhuongThucThanhToan;
import com.shop.server.entities.main.HoaDon;
import com.shop.server.entities.main.PhuongThucThanhToan;
import com.shop.server.utils.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Service
public class AdminPaymentServicesImpl implements AdminPaymentServices {
    private final AdminKhachHangPayRepository adminKhachHangRepository;

    private final AdminPhieuGiamGiaRepository adminPhieuGiamGiaRepository;

    private final AdminChiTietPhuongThucThanhToanRepository adminChiTietPhuongThucThanhToanRepository;

    private final AdPhuongThucThanhToanRepository adPhuongThucThanhToanRepository;

    private final AdminHoaDonRepository adminHoaDonRepository;

    @Override
    public ResponseObject<?> getAllKhachHang(AdminKhachHangSearchRequest request) {
        Pageable pageable = Helper.createPageable(request);
        return new ResponseObject<>(PageableObject.of(adminKhachHangRepository.getKhachHangs(pageable,request)),
                HttpStatus.OK,
                "Lấy khách hàng thành công"
        );
    }

    @Override
    public ResponseObject<?> getKhachHangById(String idKhachHang) {
        return ResponseObject.successForward(adminKhachHangRepository.getKhachHang(idKhachHang),
                "Lấy khách hàng thành công"
        );
    }


    //    @Override
//    public ResponseObject<?> getAllVoucherKhachHang(AdminHoaDonKhachHangRequest request) {
//        Pageable pageable = Helper.createPageable(request);
//        return new ResponseObject<>(adminPhieuGiamGiaRepository.getPhieuGiamGia(request,pageable),
//                HttpStatus.OK,
//                "Lấy Phiếu giảm giá thành công"
//        );
//
//    }
//    public ResponseObject<?> getAllVoucherKhachHang(AdminHoaDonKhachHangRequest request, String idKhachHang) {
//        Pageable pageable = PageRequest.of(request.getPage() - 1, request.getSize());
//            return new ResponseObject<>(PageableObject.of(adminPhieuGiamGiaRepository.getPhieuGiamGia(request,idKhachHang, pageable)), HttpStatus.OK, "Lấy danh sách phiếu giảm giá thành công");
//    }

    @Override
    public ResponseObject<?> getAllVoucherKhachHangNoId(AdminHoaDonKhachHangRequest request) {
        Pageable pageable = PageRequest.of(request.getPage() - 1, request.getSize());
        return new ResponseObject<>(PageableObject.of(adminPhieuGiamGiaRepository.getPhieuGiamGia(request, pageable)), HttpStatus.OK, "Lấy danh sách phiếu giảm giá thành công");
    }


    @Override
    public ResponseObject<?> getVoucherKhachHangById(AdminHoaDonKhachHangRequest request) {
        return ResponseObject.successForward(adminPhieuGiamGiaRepository.getPhieuGiamGiaById(request),
                "Lấy Phiếu giảm giá thành công"
        );
    }

    @Override
    public ResponseObject<?> getPhuongThucThanhToan(String idHoaDon) {
        return ResponseObject.successForward(adminChiTietPhuongThucThanhToanRepository.getAllPhuongThucThanhToan(idHoaDon),
                "Lấy phương thức thanh toán thành công"
        );
    }

    @Override
    public ResponseObject<?> savePayBill(AdminHoaDonKhachHangRequest request) {
            return null;
    }

    @Override
    public ResponseObject<?> getCustomerAddressByIdCustomer(AdminCustomerAddressSearchRequest request) {
        Pageable pageable = PageRequest.of(request.getPage() - 1, request.getSize());
        return new ResponseObject<>(PageableObject.of(adminKhachHangRepository.getCustomerAddressById(pageable, request)), HttpStatus.OK, "Lấy danh sách địa chỉ thành công");
    }

    @Override
    public ResponseObject<?> getNextTotalPriceToVoucher(AdminHoaDonKhachHangRequest request) {
        return new ResponseObject<>(adminPhieuGiamGiaRepository.findNextEligibleTongTien(request).orElse(BigDecimal.ZERO), HttpStatus.OK, "Lấy số tiền để áp dụng voucher tiếp theo thành công");
    }

    @Override
    public ResponseObject<?> addPaymentMethodDetail(AdminPaymentMethodDetailRequest request) {
        HoaDon hoaDon = request.getIdHoaDon() != null ? adminHoaDonRepository.findById(request.getIdHoaDon()).orElse(null) : null;
        PhuongThucThanhToan pttt = request.getIdPhuongThucThanhToan() != null ? adPhuongThucThanhToanRepository.findById(request.getIdPhuongThucThanhToan()).orElse(null) : null;
        ChiTietPhuongThucThanhToan ctpttt = new ChiTietPhuongThucThanhToan();
        ctpttt.setHoaDon(hoaDon);
        ctpttt.setPhuongThucThanhToan(pttt);
        ctpttt.setTienKhachDua(request.getTienKhachDua());
        ctpttt.setSoTienDu(request.getSoTienDu());
        ctpttt.setMaGiaoDich(request.getMaGiaoDich());
        ctpttt.setDeleted(false);
        ChiTietPhuongThucThanhToan ct = adminChiTietPhuongThucThanhToanRepository.save(ctpttt);
        return new ResponseObject<>(ct, HttpStatus.CREATED, "Thêm chi tiết pttt thành công");
    }
}
