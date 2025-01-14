package com.shop.server.core.admin.billdetail.repository;

import com.shop.server.core.admin.billdetail.model.request.AdminFindBillDetailRequest;
import com.shop.server.core.admin.billdetail.model.response.AdminBillDetailResponse;
import com.shop.server.entities.main.HoaDon;
import com.shop.server.entities.main.HoaDonChiTiet;
import com.shop.server.entities.main.SanPhamChiTiet;
import com.shop.server.repositories.HoaDonChiTietRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdminBillDetailRepository extends HoaDonChiTietRepository {
    @Query(value = """
        SELECT
            ROW_NUMBER() OVER(ORDER BY ct.id DESC) AS catalog,
            ct.id AS id,
            ct.id_hoa_don AS idHoaDon,
            hd.ma_hoa_don AS maHoaDon,
            ct.id_san_pham_chi_tiet AS idSanPhamChiTiet,
            spct.ten AS tenSanPhamChiTiet,
            ct.so_luong AS soLuong,
            ct.gia AS gia,
            ct.thanh_tien AS thanhTien,
            hd.trang_thai AS trangThaiHD,
            hd.tong_tien AS tongTienHD,
            hd.tien_giam AS tienGiamHD,
            hd.loai_hoa_don AS loaiHoaDon,
            hd.ten_nguoi_nhan AS tenNguoiNhan,
            hd.so_dien_thoai AS soDienThoai,
            hd.dia_chi_nguoi_nhan AS diaChiNguoiNhan,
            hd.tien_ship AS tienShip,
            hd.ngay_ship AS ngayShip,
            hd.ghi_chu AS ghiChuHD,
            hd.trang_thai AS trangThaiHD,
            nv.ho_va_ten AS tenNhanVien,
            kh.ho_va_ten AS tenKhachHang,
            kc.ten AS tenKichCo
        FROM hoa_don_chi_tiet ct
        LEFT JOIN san_pham_chi_tiet spct ON ct.id_san_pham_chi_tiet = spct.id
        JOIN hoa_don hd ON ct.id_hoa_don = hd.id
        JOIN kich_co kc ON spct.id_kich_co = kc.id
        LEFT JOIN khach_hang kh ON hd.id_khach_hang = kh.id
        LEFT JOIN nhan_vien nv ON hd.id_nhan_vien = nv.id
        LEFT JOIN phieu_giam_gia pg ON hd.id_phieu_giam_gia = pg.id
        WHERE
            (:#{#req.keyword} IS NULL OR
             spct.ten LIKE CONCAT('%', :#{#req.keyword}, '%'))
        AND (:#{#req.idHoaDon} IS NULL OR :#{#req.idHoaDon} = hd.id)
    """,countQuery = """
            SELECT
            COUNT(ct.id)
        FROM hoa_don_chi_tiet ct
        LEFT JOIN san_pham_chi_tiet spct ON ct.id_san_pham_chi_tiet = spct.id
        JOIN hoa_don hd ON ct.id_hoa_don = hd.id
        WHERE
            (:#{#req.keyword} IS NULL OR
             spct.ten LIKE CONCAT('%', :#{#req.keyword}, '%'))
        AND (:#{#req.idHoaDon} IS NULL OR :#{#req.idHoaDon} = hd.id)
    """, nativeQuery = true)
    Page<AdminBillDetailResponse> getAdminBillDetailByRequest(Pageable pageable, AdminFindBillDetailRequest req);

    @Query(value = """
        SELECT
            ct.id AS id,
            ct.id_hoa_don AS idHoaDon,
            ct.id_san_pham_chi_tiet AS idSanPhamChiTiet,
            spct.ten AS tenSanPhamChiTiet,
            ct.so_luong AS soLuong,
            ct.gia AS gia,
            ct.thanh_tien AS thanhTien
        FROM hoa_don_chi_tiet ct
        LEFT JOIN san_pham_chi_tiet spct ON ct.id_san_pham_chi_tiet = spct.id
        WHERE ct.id = :id
    """, nativeQuery = true)
    AdminBillDetailResponse getAdminBillDetailById(String id);


    Optional<HoaDonChiTiet> findByHoaDonAndSanPhamChiTiet(HoaDon hoaDon, SanPhamChiTiet sanPhamChiTiet);

}
