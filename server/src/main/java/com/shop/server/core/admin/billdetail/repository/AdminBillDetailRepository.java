package com.shop.server.core.admin.billdetail.repository;

import com.shop.server.core.admin.billdetail.model.request.AdminFindBillDetailRequest;
import com.shop.server.core.admin.billdetail.model.response.AdminBillDetailResponse;
import com.shop.server.repositories.HoaDonChiTietRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminBillDetailRepository extends HoaDonChiTietRepository {
    @Query(value = """
        SELECT
            ROW_NUMBER() OVER(ORDER BY ct.id DESC) AS catalog,
            ct.id AS id,
            ct.id_hoa_don AS idHoaDon,
            ct.id_san_pham_chi_tiet AS idSanPhamChiTiet,
            spct.ten AS tenSanPhamChiTiet,
            ct.so_luong AS soLuong,
            ct.gia AS gia,
            ct.thanh_tien AS thanhTien,
            ct.trang_thai AS trangThai
        FROM hoa_don_chi_tiet ct
        LEFT JOIN san_pham_chi_tiet spct ON ct.id_san_pham_chi_tiet = spct.id
        WHERE
            (:#{#req.keyword} IS NULL OR
             spct.ten LIKE CONCAT('%', :#{#req.keyword}, '%'))
    """,countQuery = """
            SELECT
            COUNT(ct.id),
            ct.id AS id,
            ct.id_hoa_don AS idHoaDon,
            ct.id_san_pham_chi_tiet AS idSanPhamChiTiet,
            spct.ten AS tenSanPhamChiTiet,
            ct.so_luong AS soLuong,
            ct.gia AS gia,
            ct.thanh_tien AS thanhTien,
            ct.trang_thai AS trangThai
        FROM hoa_don_chi_tiet ct
        LEFT JOIN san_pham_chi_tiet spct ON ct.id_san_pham_chi_tiet = spct.id
        WHERE
            (:#{#req.keyword} IS NULL OR
             spct.ten LIKE CONCAT('%', :#{#req.keyword}, '%'))
    """, nativeQuery = true)
    Page<AdminBillDetailResponse> getAdminBillDetailByRequest(Pageable pageable, AdminFindBillDetailRequest req);

    @Query(value = """
        SELECT
            COUNT(ct.id),
            ct.id AS id,
            ct.id_hoa_don AS idHoaDon,
            ct.id_san_pham_chi_tiet AS idSanPhamChiTiet,
            spct.ten AS tenSanPhamChiTiet,
            ct.so_luong AS soLuong,
            ct.gia AS gia,
            ct.thanh_tien AS thanhTien,
            ct.trang_thai AS trangThai
        FROM hoa_don_chi_tiet ct
        LEFT JOIN san_pham_chi_tiet spct ON ct.id_san_pham_chi_tiet = spct.id
        WHERE ct.id = :id
    """, nativeQuery = true)
    AdminBillDetailResponse getAdminBillDetailById(String id);
}
