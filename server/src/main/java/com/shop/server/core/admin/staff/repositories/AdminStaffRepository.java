package com.shop.server.core.admin.staff.repositories;

import com.shop.server.core.admin.staff.models.requests.AdminFindStaffRequest;
import com.shop.server.core.admin.staff.models.responses.AdminDetailStaffResponse;
import com.shop.server.core.admin.staff.models.responses.AdminStaffExcelResponse;
import com.shop.server.core.admin.staff.models.responses.AdminStaffResponse;
import com.shop.server.infrastructure.constants.module.Role;
import com.shop.server.repositories.NhanVienRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminStaffRepository extends NhanVienRepository {

    /**
     * Lấy dữ liệu nhân viên dựa trên các trường có trong request
     *
     * @param pageable
     * @param req
     * @return
     */
    @Query(value = """
                SELECT
                    ROW_NUMBER() OVER(ORDER BY nv.ngay_tao DESC) AS catalog,
                	nv.id AS id,
                    nv.ho_va_ten as name,
                    nv.email as email,
                    CONCAT(nv.sub_code, nv.ma_nhan_vien) as code,
                    nv.so_dien_thoai as phoneNumber,
                    nv.deleted as status
                FROM nhan_vien nv
                WHERE
                    (:#{#req.keyword} IS NULL OR
                    nv.ho_va_ten LIKE CONCAT('%', :#{#req.keyword}, '%') OR
                    nv.email LIKE CONCAT('%', :#{#req.keyword}, '%') OR
                    nv.so_dien_thoai LIKE CONCAT('%', :#{#req.keyword}, '%') OR
                    nv.ma_nhan_vien LIKE CONCAT('%', :#{#req.keyword}, '%') OR
                    nv.identity LIKE CONCAT('%', :#{#req.keyword}, '%'))
                AND (:#{#req.status} IS NULL OR nv.deleted = :#{#req.status})
                AND (nv.role = 1)
                ORDER BY nv.ngay_tao DESC
            """, countQuery = """
                SELECT
                    COUNT(nv.id)
                FROM nhan_vien nv
                WHERE
                    (:#{#req.keyword} IS NULL OR
                    nv.ho_va_ten LIKE CONCAT('%', :#{#req.keyword}, '%') OR
                    nv.email LIKE CONCAT('%', :#{#req.keyword}, '%') OR
                    nv.so_dien_thoai LIKE CONCAT('%', :#{#req.keyword}, '%') OR
                    nv.ma_nhan_vien LIKE CONCAT('%', :#{#req.keyword}, '%') OR
                    nv.identity LIKE CONCAT('%', :#{#req.keyword}, '%'))
                AND (:#{#req.status} IS NULL OR nv.deleted = :#{#req.status})
                AND (nv.role = 1)
            """, nativeQuery = true)
    Page<AdminStaffResponse> getStaffByRequest(Pageable pageable, AdminFindStaffRequest req);

    @Query(value = """
                SELECT
                    nv.id AS id,
                    nv.ma_nhan_vien AS code,
                    nv.ho_va_ten as fullName,
                    nv.ngay_sinh as birthday,
                    nv.gioi_tinh as gender,
                    nv.so_dien_thoai as phoneNumber,
                    nv.email as email,
                    nv.password as password,
                    nv.identity as identity,
                    nv.deleted as status,
                    nv.profile_picture as picture,
                    CONCAT(nt.sub_code, nt.ma_nhan_vien) AS createdBy,
                    CONCAT(ns.sub_code, ns.ma_nhan_vien) AS lastModifiedBy,
                    nv.ngay_tao as createdDate,
                    nv.ngay_sua as lastModifiedDate
                FROM nhan_vien nv
                LEFT JOIN nhan_vien nt ON nt.email = nv.nguoi_tao
                LEFT JOIN nhan_vien ns ON ns.email = nv.nguoi_tao
                WHERE nv.id = :id
            """, nativeQuery = true)
    AdminDetailStaffResponse getStaffDetail(String id);

    boolean existsStaffByEmail(String email);

    boolean existsStaffByIdentity(String identity);

    boolean existsStaffByPhoneNumber(String phoneNumber);

    @Query(value = """
            SELECT EXISTS (
                SELECT 1
                FROM nhan_vien nv
                WHERE nv.email = :email AND id != :id
            )
            """, nativeQuery = true)
    Long existsStaffByEmailAndIdNotEquals(String email, String id);

    @Query(value = """
            SELECT EXISTS (
                SELECT 1
                FROM nhan_vien nv
                WHERE nv.identity = :identity AND id != :id
            )
            """, nativeQuery = true)
    Long existsStaffByIdentityAndIdNotEquals(String identity, String id);

    @Query(value = """
            SELECT EXISTS (
                SELECT 1
                FROM nhan_vien nv
                WHERE nv.so_dien_thoai = :phoneNumber AND id != :id
            )
            """, nativeQuery = true)
    Long existsStaffByPhoneNumberAndIdNotEquals(String phoneNumber, String id);

    Long countNhanVienByRole(Role role);

    @Query(value = """
            SELECT
                ROW_NUMBER() OVER(ORDER BY nv.ngay_tao DESC) AS catalog,
                CONCAT(nv.sub_code, nv.ma_nhan_vien) as code,
                nv.ho_va_ten as name,
                nv.identity as identity,
                IF(nv.gioi_tinh = 1, 'Nam', 'Nữ') AS gender,
                DATE_FORMAT(FROM_UNIXTIME(nv.ngay_sinh / 1000), '%d-%m-%Y') AS age,
                nv.email as email,
                nv.so_dien_thoai as phone,
                IF(nv.deleted = false, 'Làm việc', 'Nghỉ') as status
            FROM nhan_vien nv
            WHERE nv.role = 1
            ORDER BY nv.ngay_tao DESC
            """, nativeQuery = true)
    List<AdminStaffExcelResponse> getStaffsExcel();

}
