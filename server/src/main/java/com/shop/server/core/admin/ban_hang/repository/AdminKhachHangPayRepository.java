package com.shop.server.core.admin.ban_hang.repository;

import com.shop.server.core.admin.ban_hang.model.request.AdminKhachHangSearchRequest;
import com.shop.server.core.admin.ban_hang.model.response.AdminKhachHangResponse;
import com.shop.server.repositories.KhachHangRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminKhachHangPayRepository extends KhachHangRepository {

    @Query(value = """
            SELECT 
            ROW_NUMBER() OVER(ORDER BY kh.ngay_tao ) AS catalog,
            kh.profile_picture as profilePicture,
            kh.id as id,
            kh.ho_va_ten as name,
            kh.email as email,
            kh.so_dien_thoai as phoneNumber,
            dckh.tinh_thanh_pho as tinh,
            dckh.quan_huyen as huyen,
            dckh.phuong_xa as xa,
            dckh.so_nha as soNha
            FROM khach_hang kh left join dia_chi_khach_hang dckh on kh.id = dckh.id_khach_hang
            WHERE   :#{#request.keyword} IS NULL OR 
                    kh.email LIKE CONCAT('%',:#{#request.keyword},'%')
                    OR kh.ho_va_ten LIKE CONCAT('%',:#{#request.keyword},'%')
""",nativeQuery = true)
    Page<AdminKhachHangResponse> getKhachHangs(Pageable pageable, AdminKhachHangSearchRequest request);

    @Query(value = """
            SELECT 
            ROW_NUMBER() OVER(ORDER BY kh.ngay_tao ) AS catalog,
            kh.profile_picture as profilePicture,
            kh.id as id,
            kh.ho_va_ten as name,
            kh.email as email,
            kh.so_dien_thoai as phoneNumber,
            dckh.tinh_thanh_pho as tinh,
            dckh.quan_huyen as huyen,
            dckh.phuong_xa as xa,
            dckh.so_nha as soNha
            FROM khach_hang kh left join dia_chi_khach_hang dckh on kh.id = dckh.id_khach_hang
            WHERE kh.id = :#{#idKhachHang}
""",nativeQuery = true)
    AdminKhachHangResponse getKhachHang(String idKhachHang);
}
