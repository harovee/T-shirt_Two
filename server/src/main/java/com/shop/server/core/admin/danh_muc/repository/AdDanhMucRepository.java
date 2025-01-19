package com.shop.server.core.admin.danh_muc.repository;

import com.shop.server.core.admin.danh_muc.model.request.AdFindDanhMucRequest;
import com.shop.server.core.admin.danh_muc.model.response.AdDanhMucResponse;
import com.shop.server.core.admin.danh_muc.model.response.AdGetDanhMucResponse;
import com.shop.server.repositories.DanhMucRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdDanhMucRepository extends DanhMucRepository {

    @Query(value = """
        SELECT 
            ROW_NUMBER() OVER(ORDER BY dm.ngay_tao DESC) AS catalog,
            dm.id as id,
            dm.ma_danh_muc as maDanhMuc,
            dm.ten AS ten,
            dm.ngay_tao AS ngayTao
        FROM danh_muc dm
        WHERE
            (:#{#req.keyword} IS NULL OR dm.ten LIKE CONCAT('%', :#{#req.keyword}, '%') OR dm.ma_danh_muc LIKE CONCAT('%', :#{#req.keyword}, '%'))
            AND dm.deleted = 0
    """, countQuery = """
        SELECT COUNT(dm.id)
        FROM danh_muc dm
        WHERE
            (:#{#req.keyword} IS NULL OR dm.ten LIKE CONCAT('%', :#{#req.keyword}, '%') OR dm.ma_danh_muc LIKE CONCAT('%', :#{#req.keyword}, '%'))
            AND dm.deleted = 0
    """, nativeQuery = true)
    Page<AdDanhMucResponse> getAllDanhMucs (Pageable pageable, AdFindDanhMucRequest req);

    @Query(value = """
    SELECT dm.id AS id,
    dm.ten AS ten
    FROM DanhMuc dm
    ORDER BY dm.createdDate DESC
""")
    List<AdGetDanhMucResponse> getListDanhMuc ();

    Boolean existsDanhMucByMaDanhMuc(String maDanhMuc);

    Boolean existsDanhMucByTen(String ten);

    @Query("""
        SELECT dm
        FROM DanhMuc dm
        WHERE dm.ten = :ten AND dm.id != :id
""")
    AdDanhMucResponse existsDanhMucByTenNotId(@Param("ten") String name, @Param("id") String id);
}
