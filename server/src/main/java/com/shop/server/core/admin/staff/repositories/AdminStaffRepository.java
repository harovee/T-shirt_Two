package com.shop.server.core.admin.staff.repositories;

import com.shop.server.core.admin.staff.models.requests.AdminFindStaffRequest;
import com.shop.server.core.admin.staff.models.responses.AdminDetailStaffResponse;
import com.shop.server.core.admin.staff.models.responses.AdminStaffResponse;
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
                    nv.email LIKE CONCAT('%', :#{#req.keyword}, '%'))
                AND (:#{#req.status} IS NULL OR nv.deleted = :#{#req.status})
                ORDER BY nv.ngay_tao DESC
            """, countQuery = """
                SELECT
                    COUNT(nv.id)
                FROM nhan_vien nv
                WHERE
                    (:#{#req.keyword} IS NULL OR
                    nv.ho_va_ten LIKE CONCAT('%', :#{#req.keyword}, '%') OR
                    nv.email LIKE CONCAT('%', :#{#req.keyword}, '%'))
                AND (:#{#req.status} IS NULL OR nv.deleted = :#{#req.status})
            """, nativeQuery = true)
    Page<AdminStaffResponse> getStaffByRequest(Pageable pageable, AdminFindStaffRequest req);

    @Query(value = """
                SELECT
                    nv.id AS id,
                    nv.ho_va_ten as name,
                    nv.email as email,
                    nv.deleted as status,
                    nv.ma_nhan_vien as code,
                    nv.gioi_tinh as sex,
                    nv.profile_picture as picture,
                    nv.password as password,
                    nv.ngay_sinh as birthday,
                    nv.so_dien_thoai as phoneNumber,
                    nv.nguoi_sua as updateBy,
                    nv.nguoi_tao as createBY
                FROM nhan_vien nv
                WHERE nv.id = :id
            """, nativeQuery = true)
    AdminDetailStaffResponse getStaffDetail(String id);

    boolean existsStaffByEmail(String email);
}
