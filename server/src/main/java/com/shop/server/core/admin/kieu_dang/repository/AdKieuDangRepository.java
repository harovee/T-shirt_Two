package com.shop.server.core.admin.kieu_dang.repository;

import com.shop.server.core.admin.kieu_dang.model.request.AdFindKieuDangRequest;
import com.shop.server.core.admin.kieu_dang.model.response.AdGetKieuDangResponse;
import com.shop.server.core.admin.kieu_dang.model.response.AdKieuDangResponse;
import com.shop.server.repositories.KieuDangRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdKieuDangRepository extends KieuDangRepository {

    @Query(value = """
        SELECT 
            ROW_NUMBER() OVER(ORDER BY kd.ngay_tao DESC) AS catalog,
            kd.id as id,
            kd.ma_kieu_dang as maKieuDang,
            kd.ten AS ten,
            kd.ngay_tao AS ngayTao
        FROM kieu_dang kd
        WHERE
            (:#{#req.keyword} IS NULL OR kd.ten LIKE CONCAT('%', :#{#req.keyword}, '%') OR kd.ma_kieu_dang LIKE CONCAT('%', :#{#req.keyword}, '%'))
            AND kd.deleted = 0
    """, countQuery = """
        SELECT COUNT(kd.id)
        FROM kieu_dang kd
        WHERE
            (:#{#req.keyword} IS NULL OR kd.ten LIKE CONCAT('%', :#{#req.keyword}, '%') OR kd.ma_kieu_dang LIKE CONCAT('%', :#{#req.keyword}, '%'))
            AND kd.deleted = 0
    """, nativeQuery = true)
    Page<AdKieuDangResponse> getAllKieuDangs (Pageable pageable, AdFindKieuDangRequest req);

    @Query(value = """
    SELECT kd.id AS id,
    kd.ten AS ten
    FROM KieuDang kd
    ORDER BY kd.createdDate DESC
""")
    List<AdGetKieuDangResponse> getListKieuDang ();

    Boolean existsKieuDangByMaKieuDang(String maKieuDang);

    Boolean existsKieuDangByTen(String ten);

    @Query("""
        SELECT kd
        FROM KieuDang kd
        WHERE kd.ten = :ten AND kd.id != :id
""")
    AdKieuDangResponse existsKieuDangByTenNotId(@Param("ten") String name, @Param("id") String id);
}
