package com.shop.server.core.admin.staff.repositories;

import com.shop.server.core.admin.staff.models.requests.AdminFindStaffRequest;
import com.shop.server.core.admin.staff.models.responses.AdminDetailStaffResponse;
import com.shop.server.core.admin.staff.models.responses.AdminStaffResponse;
import com.shop.server.infrastructure.constants.module.Role;
import com.shop.server.repositories.NhanVienRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

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
                    nv.ma_nhan_vien as code,
                    nv.deleted as status
                FROM nhan_vien nv
                WHERE
                    (:#{#req.keyword} IS NULL OR
                    nv.ho_va_ten LIKE CONCAT('%', :#{#req.keyword}, '%') OR
                    nv.email LIKE CONCAT('%', :#{#req.keyword}, '%') OR
                    nv.so_dien_thoai LIKE CONCAT('%', :#{#req.keyword}, '%') OR
                    nv.ma_nhan_vien LIKE CONCAT('%', :#{#req.keyword}, '%') OR
                    nv.identity LIKE CONCAT('%', :#{#req.keyword}, '%') OR
                    nv.username LIKE CONCAT('%', :#{#req.keyword}, '%'))
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
                    nv.identity LIKE CONCAT('%', :#{#req.keyword}, '%') OR
                    nv.username LIKE CONCAT('%', :#{#req.keyword}, '%'))
                AND (:#{#req.status} IS NULL OR nv.deleted = :#{#req.status})
                AND (nv.role = 1)
            """, nativeQuery = true)
    Page<AdminStaffResponse> getStaffByRequest(Pageable pageable, AdminFindStaffRequest req);

    @Query(value = """
                SELECT
                    nv.id AS id,
                    nv.username AS username,
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
                    IFNULL('AD' + nt.ma_nhan_vien, 'Mã chưa xác định') as createdBy,
                    IFNULL('AD' + ns.ma_nhan_vien, 'Mã chưa xác định') as lastModifiedBy,
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

    boolean existsStaffByUsername(String username);

    boolean existsStaffByPhoneNumber(String phoneNumber);

    Long countNhanVienByRole(Role role);
}
