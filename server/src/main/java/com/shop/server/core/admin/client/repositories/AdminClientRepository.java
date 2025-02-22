package com.shop.server.core.admin.client.repositories;

import com.shop.server.core.admin.client.models.requests.AdminFindClientRequest;
import com.shop.server.core.admin.client.models.responses.AdminAddressByClientIdResponse;
import com.shop.server.core.admin.client.models.responses.AdminClientResponse;
import com.shop.server.core.admin.client.models.responses.AdminCommonAddressResponse;
import com.shop.server.core.admin.client.models.responses.AdminDetailClientResponse;
import com.shop.server.repositories.KhachHangRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminClientRepository extends KhachHangRepository {

    /**
     * Lấy dữ liệu khách hàng dựa trên các trường có trong request
     *
     * @param pageable
     * @param req
     * @return
     */
    @Query(value = """
            SELECT
                ROW_NUMBER() OVER(ORDER BY kh.ngay_tao DESC) AS catalog,
                kh.id AS id,
                kh.ho_va_ten as name,
                kh.email as email,
                kh.ma_khach_hang as code,
                kh.so_dien_thoai as phoneNumber,
                kh.deleted as status,
                dckh.so_nha as address
            FROM khach_hang kh
            LEFT JOIN dia_chi_khach_hang dckh ON ( kh.id = dckh.id_khach_hang AND dckh.mac_dinh = 1)
            WHERE
                (:#{#req.keyword} IS NULL OR
                kh.ho_va_ten LIKE CONCAT('%', :#{#req.keyword}, '%') OR
                kh.email LIKE CONCAT('%', :#{#req.keyword}, '%') OR
                kh.so_dien_thoai LIKE CONCAT('%', :#{#req.keyword}, '%') OR
                kh.ma_khach_hang LIKE CONCAT('%', :#{#req.keyword}, '%'))
            AND (:#{#req.status} IS NULL OR kh.deleted = :#{#req.status})
            ORDER BY kh.ngay_tao DESC
            """, countQuery = """
            SELECT
                COUNT(kh.id)
            FROM khach_hang kh
            LEFT JOIN dia_chi_khach_hang dckh ON ( kh.id = dckh.id_khach_hang AND dckh.mac_dinh = 1)
            WHERE
                (:#{#req.keyword} IS NULL OR
                kh.ho_va_ten LIKE CONCAT('%', :#{#req.keyword}, '%') OR
                kh.email LIKE CONCAT('%', :#{#req.keyword}, '%') OR
                kh.so_dien_thoai LIKE CONCAT('%', :#{#req.keyword}, '%') OR
                kh.ma_khach_hang LIKE CONCAT('%', :#{#req.keyword}, '%'))
            AND (:#{#req.status} IS NULL OR kh.deleted = :#{#req.status})
            """, nativeQuery = true)
    Page<AdminClientResponse> getClientByRequest(Pageable pageable, AdminFindClientRequest req);

    @Query(value = """
            SELECT
                kh.id AS id,
                kh.ma_khach_hang AS code,
                kh.ho_va_ten as fullName,
                kh.ngay_sinh as birthday,
                kh.gioi_tinh as gender,
                kh.so_dien_thoai as phoneNumber,
                kh.email as email,
                kh.password as password,
                kh.deleted as status,
                kh.profile_picture as picture,
                CASE
                    WHEN nt.ma_nhan_vien IS NOT NULL THEN CONCAT('SYS', nt.ma_nhan_vien)
                    ELSE CONCAT('KH', kh.ma_khach_hang)
                END AS createdBy,
                CASE
                    WHEN ns.ma_nhan_vien IS NOT NULL THEN CONCAT('SYS', ns.ma_nhan_vien)
                    ELSE CONCAT('KH', kh.ma_khach_hang)
                END AS lastModifiedBy,
                kh.ngay_tao as createdDate,
                kh.ngay_sua as lastModifiedDate
            FROM khach_hang kh
            LEFT JOIN nhan_vien nt ON nt.id = kh.nguoi_tao
            LEFT JOIN nhan_vien ns ON ns.id = kh.nguoi_sua
            WHERE kh.id = :id
            """, nativeQuery = true)
    AdminDetailClientResponse getClientDetail(String id);

    boolean existsClientByEmail(String email);

    boolean existsClientByPhoneNumber(String phoneNumber);

    @Query(value = """
            SELECT EXISTS (
                SELECT 1
                FROM khach_hang kh
                WHERE kh.email = :email AND id != :id
            )
            """, nativeQuery = true)
    Long existsClientByEmailAndIdNotEquals(String email, String id);

    @Query(value = """
            SELECT EXISTS (
                SELECT 1
                FROM khach_hang kh
                WHERE kh.so_dien_thoai = :phoneNumber AND id != :id
            )
            """, nativeQuery = true)
    Long existsClientByPhoneNumberAndIdNotEquals(String phoneNumber, String id);

    // ** Filter province - district - ward ** \\

    @Query(value = """
            SELECT
                pro.id AS id,
                pro.name AS name
            FROM province pro
            """, nativeQuery = true)
    List<AdminCommonAddressResponse> getProvinces();

    @Query(value = """
            SELECT
                dis.id AS id,
                dis.name AS name
            FROM district dis
            WHERE dis.province_id = :id
            """, nativeQuery = true)
    List<AdminCommonAddressResponse> getDistrictsByProvinceId(Long id);

    @Query(value = """
            SELECT
                w.code AS id,
                w.name AS name
            FROM ward w
            WHERE w.district_id = :id
            """, nativeQuery = true)
    List<AdminCommonAddressResponse> getWardsByDistrictsId(Long id);

    @Modifying
    @Query(value = """
            UPDATE dia_chi_khach_hang dckh
            SET dckh.mac_dinh = false
            WHERE dckh.id_khach_hang = :clientId
            """, nativeQuery = true)
    void disableIsDefaultAddressByClientId(String clientId);

    @Modifying
    @Query(value = """
            UPDATE dia_chi_khach_hang dckh
            SET dckh.mac_dinh = true
            WHERE dckh.id = :id
            """, nativeQuery = true)
    void enableIsDefaultAddressById(String id);

    @Query(value = """
            SELECT
                dckh.id AS id,
                dckh.ten AS name,
                dckh.so_dien_thoai AS phoneNumber,
                dckh.so_nha AS line,
                dckh.phuong_xa AS ward,
                dckh.quan_huyen AS district,
                dckh.tinh_thanh_pho AS province,
                dckh.mac_dinh AS isDefault
            FROM dia_chi_khach_hang dckh
            WHERE dckh.id_khach_hang = :clientId
            ORDER BY dckh.mac_dinh DESC
            """, nativeQuery = true)
    List<AdminAddressByClientIdResponse> getAddressByClientId(String clientId);

}
