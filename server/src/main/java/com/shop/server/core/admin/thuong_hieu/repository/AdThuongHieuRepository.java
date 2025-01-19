package com.shop.server.core.admin.thuong_hieu.repository;

import com.shop.server.core.admin.thuong_hieu.model.request.AdFindThuongHieuRequest;
import com.shop.server.core.admin.thuong_hieu.model.response.AdGetThuongHieuResponse;
import com.shop.server.core.admin.thuong_hieu.model.response.AdThuongHieuResponse;
import com.shop.server.repositories.ThuongHieuRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdThuongHieuRepository extends ThuongHieuRepository {

    @Query(value = """
        SELECT 
            ROW_NUMBER() OVER(ORDER BY th.ngay_tao DESC) AS catalog,
            th.id as id,
            th.ma_thuong_hieu as maThuongHieu,
            th.ten AS ten,
            th.ngay_tao AS ngayTao
        FROM thuong_hieu th
        WHERE
            (:#{#req.keyword} IS NULL OR th.ten LIKE CONCAT('%', :#{#req.keyword}, '%') OR th.ma_thuong_hieu LIKE CONCAT('%', :#{#req.keyword}, '%'))
            AND th.deleted = 0
    """, countQuery = """
        SELECT COUNT(th.id)
        FROM thuong_hieu th
        WHERE
            (:#{#req.keyword} IS NULL OR th.ten LIKE CONCAT('%', :#{#req.keyword}, '%') OR th.ma_thuong_hieu LIKE CONCAT('%', :#{#req.keyword}, '%'))
            AND th.deleted = 0
    """, nativeQuery = true)
    Page<AdThuongHieuResponse> getAllThuongHieus (Pageable pageable, AdFindThuongHieuRequest req);

    @Query(value = """
    SELECT th.id AS id,
    th.ten AS ten
    FROM ThuongHieu th
    ORDER BY th.createdDate DESC
""")
    List<AdGetThuongHieuResponse> getListThuongHieu ();

    Boolean existsThuongHieuByMaThuongHieu(String maThuongHieu);

    Boolean existsThuongHieuByTen(String ten);

    @Query("""
        SELECT th
        FROM ThuongHieu th
        WHERE th.ten = :ten AND th.id != :id
""")
    AdThuongHieuResponse existsThuongHieuByTenNotId(@Param("ten") String name, @Param("id") String id);
}
