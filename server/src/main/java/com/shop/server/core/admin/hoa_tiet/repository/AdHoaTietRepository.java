package com.shop.server.core.admin.hoa_tiet.repository;

import com.shop.server.core.admin.hoa_tiet.model.request.AdFindHoaTietRequest;
import com.shop.server.core.admin.hoa_tiet.model.response.AdHoaTietResponse;
import com.shop.server.core.admin.hoa_tiet.model.response.AdGetHoaTietResponse;
import com.shop.server.repositories.HoaTietRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdHoaTietRepository extends HoaTietRepository {

    @Query(value = """
        SELECT 
            ROW_NUMBER() OVER(ORDER BY ht.ngay_tao DESC) AS catalog,
            ht.id as id,
            ht.ma_hoa_tiet as maHoaTiet,
            ht.ten AS ten,
            ht.ngay_tao AS ngayTao
        FROM hoa_tiet ht
        WHERE
            (:#{#req.keyword} IS NULL OR ht.ten LIKE CONCAT('%', :#{#req.keyword}, '%') OR ht.ma_hoa_tiet LIKE CONCAT('%', :#{#req.keyword}, '%'))
            AND ht.deleted = 0
    """, countQuery = """
        SELECT COUNT(ht.id)
        FROM hoa_tiet ht
        WHERE
            (:#{#req.keyword} IS NULL OR ht.ten LIKE CONCAT('%', :#{#req.keyword}, '%') OR ht.ma_hoa_tiet LIKE CONCAT('%', :#{#req.keyword}, '%'))
            AND ht.deleted = 0
    """, nativeQuery = true)
    Page<AdHoaTietResponse> getAllHoaTiets (Pageable pageable, AdFindHoaTietRequest req);

    @Query(value = """
    SELECT ht.id AS id,
    ht.ten AS ten
    FROM HoaTiet ht
    ORDER BY ht.createdDate DESC
""")
    List<AdGetHoaTietResponse> getListHoaTiet ();

    Boolean existsHoaTietByMaHoaTiet(String maHoaTiet);

    Boolean existsHoaTietByTen(String ten);

    @Query("""
        SELECT ht
        FROM HoaTiet ht
        WHERE ht.ten = :ten AND ht.id != :id
""")
    AdHoaTietResponse existsHoaTietByTenNotId(@Param("ten") String name, @Param("id") String id);
}
