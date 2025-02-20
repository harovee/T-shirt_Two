package com.shop.server.core.admin.ban_hang.repository;

import com.shop.server.core.admin.ban_hang.model.response.AdminPhuongThucThanhToanResponse;
import com.shop.server.repositories.ChiTietPhuongThucThanhToanRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminChiTietPhuongThucThanhToanRepository extends ChiTietPhuongThucThanhToanRepository {
//    @Query(value = """
//            SELECT
//                ROW_NUMBER() over (ORDER BY ctpttt.ngay_tao DESC) as catalog,
//                    ctpttt.id as id,
//                   pttt.ten_phuong_thuc as tenPhuongThuc,
//                   ctpttt.ma_giao_dich as maGiaoDich,
//                   ctpttt.tien_khach_dua as soTien
//            FROM chi_tiet_phuong_thuc_thanh_toan ctpttt LEFT JOIN phuong_thuc_thanh_toan pttt ON ctpttt.id_phuong_thuc_thanh_toan = pttt.id
//            WHERE ctpttt.id_hoa_don = :idHoaDon
//                AND ctpttt.deleted = false
//""",nativeQuery = true)
//    List<AdminPhuongThucThanhToanResponse> getAllPhuongThucThanhToan(@Param("idHoaDon") String idHoaDon);
@Query(value = """
        SELECT 
            ROW_NUMBER() OVER (ORDER BY ctpttt.ngay_tao DESC) as catalog,
            ctpttt.id as id,
            pttt.ten_phuong_thuc as tenPhuongThuc,
            ctpttt.ma_giao_dich as maGiaoDich,
            ctpttt.tien_khach_dua as soTien
        FROM chi_tiet_phuong_thuc_thanh_toan ctpttt 
        LEFT JOIN phuong_thuc_thanh_toan pttt ON ctpttt.id_phuong_thuc_thanh_toan = pttt.id
        WHERE (:idHoaDon IS NOT NULL AND :idHoaDon <> '' AND ctpttt.id_hoa_don = :idHoaDon)
            AND ctpttt.deleted = false
    """, nativeQuery = true)
List<AdminPhuongThucThanhToanResponse> getAllPhuongThucThanhToan(@Param("idHoaDon") String idHoaDon);

}
