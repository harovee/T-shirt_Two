package com.shop.server.core.admin.phieugiamgia.repository;

import com.shop.server.core.admin.phieugiamgia.model.request.AdVoucherKhachHangRequest;
import com.shop.server.core.admin.phieugiamgia.model.request.AdminKhachHangSearchRequest;
import com.shop.server.core.admin.phieugiamgia.model.response.AdKhachHangResponse;
import com.shop.server.repositories.KhachHangRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdminKhachHangRepository extends KhachHangRepository {

    @Query(value = """
            SELECT 
                ROW_NUMBER() OVER(ORDER BY kh.ngay_tao DESC) AS catalog,
                kh.id as id,
                kh.ho_va_ten as name,
                kh.email as email,
                kh.so_dien_thoai as phone,
                kh.ngay_sinh as ngaySinh
            FROM khach_hang kh
            WHERE 
                :#{#request.keyword} IS NULL
                OR kh.ho_va_ten LIKE CONCAT('%',:#{#request.keyword}, '%' )
                OR kh.email LIKE CONCAT('%',:#{#request.keyword}, '%')
                OR kh.so_dien_thoai LIKE CONCAT('%',:#{#request.keyword}, '%')
               AND kh.deleted =0
""",nativeQuery = true)
    Page<AdKhachHangResponse> getListKhachHang(Pageable pageable, AdminKhachHangSearchRequest request);

    @Query(value = """
                 SELECT 
                    ROW_NUMBER() OVER(ORDER BY kh.ngay_tao DESC) AS catalog,
                    kh.id as id,
                    kh.ho_va_ten as name,
                    kh.email as email,
                    kh.so_dien_thoai as phone
                FROM khach_hang kh
                WHERE 
                    kh.id = :#{#idKhachHang}
                    AND kh.delete =0 
            """,nativeQuery = true)
    Optional<AdKhachHangResponse> getKhachHangById(String idKhachHang);

    @Query(value = """
                SELECT   
                    ROW_NUMBER() OVER(ORDER BY kh.ngay_tao DESC) AS catalog,
                    kh.id as id,
                    kh.ho_va_ten as name,
                    kh.email as email,
                    kh.so_dien_thoai as phone,
                    kh.ngay_sinh as ngaySinh
                FROM khach_hang kh join khach_hang_phieu_giam_gia pgg on kh.id = pgg.id_khach_hang
                WHERE pgg.id_phieu_giam_gia = :#{#idPhieuGiamGia}
""",nativeQuery = true)
    List<AdKhachHangResponse> getListKhachHangInVoucher(String idPhieuGiamGia);
}
