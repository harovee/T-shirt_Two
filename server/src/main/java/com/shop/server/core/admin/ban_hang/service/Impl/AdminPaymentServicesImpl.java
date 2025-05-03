package com.shop.server.core.admin.ban_hang.service.Impl;

import com.shop.server.core.admin.ban_hang.model.request.*;
import com.shop.server.core.admin.ban_hang.repository.*;
import com.shop.server.core.admin.ban_hang.service.AdminPaymentServices;
import com.shop.server.core.admin.client.repositories.AdminClientDistrictRepository;
import com.shop.server.core.admin.client.repositories.AdminClientProvinceRepository;
import com.shop.server.core.admin.client.repositories.AdminClientWardRepository;
import com.shop.server.core.common.base.PageableObject;
import com.shop.server.core.common.base.ResponseObject;
import com.shop.server.entities.main.ChiTietPhuongThucThanhToan;
import com.shop.server.entities.main.HoaDon;
import com.shop.server.entities.main.LichSuHoaDon;
import com.shop.server.entities.main.PhuongThucThanhToan;
import com.shop.server.repositories.HoaDonRepository;
import com.shop.server.repositories.LichSuHoaDonRepository;
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

    private final AdminClientProvinceRepository adminClientProvinceRepository;

    private final AdminClientWardRepository adminClientWardRepository;

    private final AdminClientDistrictRepository adminClientDistrictRepository;
    private final HoaDonRepository hoaDonRepository;
    private final LichSuHoaDonRepository lichSuHoaDonRepository;

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
    public ResponseObject<?> getPhuongThucThanhToan(AdminPaymentMethodDetailRequest request) {
        return ResponseObject.successForward(adminChiTietPhuongThucThanhToanRepository.getAllPhuongThucThanhToan(request),
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
        if (request.getIdPhuongThucThanhToan().equals("cahai")) {
            PhuongThucThanhToan pttt1 = adPhuongThucThanhToanRepository.findById("tienmat").orElse(null);
            PhuongThucThanhToan pttt2 = adPhuongThucThanhToanRepository.findById("chuyenkhoan").orElse(null);
            ChiTietPhuongThucThanhToan ctpttt1 = new ChiTietPhuongThucThanhToan();
            ctpttt1.setHoaDon(hoaDon);
            ctpttt1.setPhuongThucThanhToan(pttt1);
            ctpttt1.setTienKhachDua(request.getTienKhachDua());
            ctpttt1.setNguoiTao(request.getIdNhanVien());
            ctpttt1.setDeleted(false);
            ChiTietPhuongThucThanhToan ctpttt2 = new ChiTietPhuongThucThanhToan();
            ctpttt2.setHoaDon(hoaDon);
            ctpttt2.setPhuongThucThanhToan(pttt2);
            ctpttt2.setTienKhachDua(request.getTienChuyenKhoan());
            ctpttt2.setMaGiaoDich(request.getMaGiaoDich());
            ctpttt2.setNguoiTao(request.getIdNhanVien());
            ctpttt2.setDeleted(false);
            ChiTietPhuongThucThanhToan ct = adminChiTietPhuongThucThanhToanRepository.save(ctpttt1);
            // Nếu chuyển khoản 0 đồng thì k lưu
            if (ctpttt2.getTienKhachDua() != null && ctpttt2.getTienKhachDua().compareTo(BigDecimal.ZERO) > 0) {
                ChiTietPhuongThucThanhToan ct2 = adminChiTietPhuongThucThanhToanRepository.save(ctpttt2);
            }

            hoaDon.setTrangThai("Đã thanh toán");
            HoaDon hd1 = hoaDonRepository.save(hoaDon);

            LichSuHoaDon ls = new LichSuHoaDon();
            ls.setIdHoaDon(hoaDon);
            ls.setHanhDong("Thanh toán");
            ls.setMoTa(request.getMoTa());
            ls.setTrangThai(hoaDon.getTrangThai());
            LichSuHoaDon ls1 = lichSuHoaDonRepository.save(ls);
            return new ResponseObject<>("ok", HttpStatus.CREATED, "Thanh toán thành công.");
        }

        PhuongThucThanhToan pttt = request.getIdPhuongThucThanhToan() != null ? adPhuongThucThanhToanRepository.findById(request.getIdPhuongThucThanhToan()).orElse(null) : null;
        ChiTietPhuongThucThanhToan ctpttt = new ChiTietPhuongThucThanhToan();
        ctpttt.setHoaDon(hoaDon);
        ctpttt.setPhuongThucThanhToan(pttt);
        ctpttt.setTienKhachDua(request.getTienKhachDua());
        ctpttt.setSoTienDu(request.getSoTienDu());
        ctpttt.setMaGiaoDich(request.getMaGiaoDich());
        ctpttt.setNguoiTao(request.getIdNhanVien());
        ctpttt.setDeleted(false);
        ChiTietPhuongThucThanhToan ct = adminChiTietPhuongThucThanhToanRepository.save(ctpttt);

        hoaDon.setTrangThai("Đã thanh toán");
        HoaDon hd1 = hoaDonRepository.save(hoaDon);


        LichSuHoaDon ls = new LichSuHoaDon();
        ls.setIdHoaDon(hoaDon);
        ls.setHanhDong("Thanh toán");
        ls.setMoTa(request.getMoTa());
        ls.setTrangThai(hoaDon.getTrangThai());
        LichSuHoaDon ls1 = lichSuHoaDonRepository.save(ls);
        return new ResponseObject<>(ct, HttpStatus.CREATED, "Thanh toán thành công.");

    }

    @Override
    public ResponseObject<?> getCustomerByPhoneNumber(String phoneNumber) {
        return new ResponseObject<>(adminKhachHangRepository.getKhachHangByPhoneNumber(phoneNumber), HttpStatus.OK, "Lấy khách hàng theo sdt thành công");
    }

    @Override
    public ResponseObject<?> getWardByCode(String code) {
        return new ResponseObject<>(adminClientWardRepository.getWardNameByCode(code), HttpStatus.OK, "Lấy xã theo code thành công");
    }

    @Override
    public ResponseObject<?> getDistrictById(String id) {
            return new ResponseObject<>(adminClientDistrictRepository.getDistrictsById(id), HttpStatus.OK, "Lấy huyện theo code thành công");
    }

    @Override
    public ResponseObject<?> getProvinceById(String id) {
        return new ResponseObject<>(adminClientProvinceRepository.getProvinceById(id), HttpStatus.OK, "Lấy tỉnh theo code thành công");
    }

    @Override
    public ResponseObject<?> getVoucherByCode(AdminVoucherRequest request) {
        return ResponseObject.successForward(adminPhieuGiamGiaRepository.getPhieuGiamGiaByCode(request),
                "Lấy Phiếu giảm giá thành công"
        );
    }
}
