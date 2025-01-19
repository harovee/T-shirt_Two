package com.shop.server.core.admin.client.repositories;

import com.shop.server.core.admin.client.models.requests.AdminFindClientRequest;
import com.shop.server.core.admin.client.models.responses.AdminClientResponse;
import com.shop.server.core.admin.client.models.responses.AdminDetailClientResponse;
import com.shop.server.repositories.KhachHangRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

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
                    ROW_NUMBER() OVER(ORDER BY kh.id DESC) AS catalog,
                	kh.id AS id,
                    kh.ho_va_ten as name,
                    kh.email as email,
                    kh.ma_khach_hang as code,
                    kh.so_dien_thoai as phoneNumber,
                    kh.deleted as status
                FROM khach_hang kh
                WHERE
                    (:#{#req.keyword} IS NULL OR
                    kh.ho_va_ten LIKE CONCAT('%', :#{#req.keyword}, '%') OR
                    kh.email LIKE CONCAT('%', :#{#req.keyword}, '%') OR
                    kh.so_dien_thoai LIKE CONCAT('%', :#{#req.keyword}, '%') OR
                    kh.ma_khach_hang LIKE CONCAT('%', :#{#req.keyword}, '%') OR
                    kh.username LIKE CONCAT('%', :#{#req.keyword}, '%'))
                AND (:#{#req.status} IS NULL OR kh.deleted = :#{#req.status})
                ORDER BY kh.ngay_tao DESC
            """, countQuery = """
                SELECT
                    COUNT(kh.id)
                FROM khach_hang kh
                WHERE
                    (:#{#req.keyword} IS NULL OR
                    kh.ho_va_ten LIKE CONCAT('%', :#{#req.keyword}, '%') OR
                    kh.email LIKE CONCAT('%', :#{#req.keyword}, '%') OR
                    kh.so_dien_thoai LIKE CONCAT('%', :#{#req.keyword}, '%') OR
                    kh.ma_khach_hang LIKE CONCAT('%', :#{#req.keyword}, '%') OR
                    kh.username LIKE CONCAT('%', :#{#req.keyword}, '%'))
                AND (:#{#req.status} IS NULL OR kh.deleted = :#{#req.status})
                ORDER BY kh.ngay_tao DESC
            """, nativeQuery = true)
    Page<AdminClientResponse> getClientByRequest(Pageable pageable, AdminFindClientRequest req);

    @Query(value = """
                SELECT
                    kh.id AS id,
                    kh.username AS username,
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
                        WHEN nt.ma_nhan_vien IS NOT NULL THEN CONCAT('AD', nt.ma_nhan_vien)
                        ELSE CONCAT('KH', kh.email)
                    END AS createdBy,
                    CASE
                        WHEN ns.ma_nhan_vien IS NOT NULL THEN CONCAT('AD', ns.ma_nhan_vien)
                        ELSE CONCAT('KH', kh.email)
                    END AS lastModifiedBy,
                    kh.ngay_tao as createdDate,
                    kh.ngay_sua as lastModifiedDate
                FROM khach_hang kh
                LEFT JOIN nhan_vien nt ON nt.email = kh.nguoi_tao
                LEFT JOIN nhan_vien ns ON ns.email = kh.nguoi_tao
                WHERE kh.id = :id
            """, nativeQuery = true)
    AdminDetailClientResponse getClientDetail(String id);

    boolean existsClientByEmail(String email);

    boolean existsClientByUsername(String username);

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
                WHERE kh.username = :username AND id != :id
            )
            """, nativeQuery = true)
    Long existsClientByUsernameAndIdNotEquals(String username, String id);

    @Query(value = """
            SELECT EXISTS (
                SELECT 1
                FROM khach_hang kh
                WHERE kh.so_dien_thoai = :phoneNumber AND id != :id
            )
            """, nativeQuery = true)
    Long existsClientByPhoneNumberAndIdNotEquals(String phoneNumber, String id);


}
