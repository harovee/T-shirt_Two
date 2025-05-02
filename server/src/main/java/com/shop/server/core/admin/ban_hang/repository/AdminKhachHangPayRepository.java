package com.shop.server.core.admin.ban_hang.repository;

import com.shop.server.core.admin.ban_hang.model.request.AdminCustomerAddressSearchRequest;
import com.shop.server.core.admin.ban_hang.model.request.AdminKhachHangSearchRequest;
import com.shop.server.core.admin.ban_hang.model.request.AdminVoucherRequest;
import com.shop.server.core.admin.ban_hang.model.response.AdminCustomerAddressResponse;
import com.shop.server.core.admin.ban_hang.model.response.AdminKhachHangResponse;
import com.shop.server.core.admin.client.models.responses.AdminAddressByClientIdResponse;
import com.shop.server.entities.main.KhachHang;
import com.shop.server.repositories.KhachHangRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminKhachHangPayRepository extends KhachHangRepository {

    @Query(value = """
            SELECT DISTINCT\s
                ROW_NUMBER() OVER(ORDER BY kh.ngay_tao ) AS catalog,
                kh.profile_picture as profilePicture,
                kh.id as id,
                kh.ho_va_ten as name,
                kh.email as email,
                kh.so_dien_thoai as phoneNumber,
                JSON_ARRAYAGG(
                    JSON_OBJECT(
                        'tinh', dckh.tinh_thanh_pho,
                        'huyen', dckh.quan_huyen,
                        'xa', dckh.phuong_xa,
                        'soNha', dckh.so_nha
                    )
                ) as diaChi
            FROM khach_hang kh\s
            LEFT JOIN dia_chi_khach_hang dckh ON kh.id = dckh.id_khach_hang
            WHERE :#{#request.keyword} IS NULL OR\s
                  kh.email LIKE CONCAT('%',:#{#request.keyword},'%')
                  OR kh.so_dien_thoai LIKE CONCAT('%',:#{#request.keyword},'%')
                  OR kh.ho_va_ten LIKE CONCAT('%',:#{#request.keyword},'%')
            GROUP BY kh.id, kh.profile_picture, kh.ho_va_ten, kh.email, kh.so_dien_thoai, kh.ngay_tao
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

    @Query(value = """
            SELECT
                ROW_NUMBER() over (ORDER BY dckh.ngay_tao) AS catalog,
                dckh.id AS id,
                dckh.ten AS name,
                dckh.so_dien_thoai AS phoneNumber,
                dckh.so_nha AS line,
                dckh.phuong_xa AS ward,
                dckh.quan_huyen AS district,
                dckh.tinh_thanh_pho AS province,
                dckh.mac_dinh AS isDefault
            FROM dia_chi_khach_hang dckh
            WHERE dckh.id_khach_hang = :#{#request.idKhachHang}
            AND (:#{#request.keyword} IS NULL OR dckh.ten LIKE CONCAT('%', :#{#request.keyword},'%')
                        OR dckh.so_dien_thoai LIKE CONCAT('%', :#{#request.keyword},'%')
                        OR dckh.so_nha LIKE CONCAT('%', :#{#request.keyword},'%'))
            ORDER BY dckh.mac_dinh DESC
            """, nativeQuery = true)
    Page<AdminCustomerAddressResponse> getCustomerAddressById(Pageable pageable, AdminCustomerAddressSearchRequest request);

    @Query(value = """
            SELECT
                ROW_NUMBER() OVER(ORDER BY kh.ngay_tao ) AS catalog,
            kh.profile_picture as profilePicture,
            kh.id as id,
            kh.ho_va_ten as name,
            kh.email as email,
            kh.so_dien_thoai as phoneNumber
            FROM khach_hang kh
            WHERE kh.so_dien_thoai = :phoneNumber
            """, nativeQuery = true)
    AdminKhachHangResponse getKhachHangByPhoneNumber(String phoneNumber);
}
