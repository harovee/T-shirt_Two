package com.shop.server.core.admin.kich_co.repository;

import com.shop.server.core.admin.kich_co.model.request.AdFindKichCoRequest;
import com.shop.server.core.admin.kich_co.model.response.AdGetKichCoResponse;
import com.shop.server.core.admin.kich_co.model.response.AdKichCoResponse;
import com.shop.server.repositories.KichCoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdKichCoRepository extends KichCoRepository {

    @Query(value = """
        SELECT 
            ROW_NUMBER() OVER(ORDER BY kc.ngay_tao DESC) AS catalog,
            kc.id as id,
            kc.ma_kich_co as maKichCo,
            kc.ten AS ten,
            kc.chieu_cao_min as chieuCaoMin,
            kc.chieu_cao_max as chieuCaoMax,
            kc.can_nang_min as canNangMin,
            kc.can_nang_max as canNangMax,
            kc.ngay_tao AS ngayTao
        FROM kich_co kc
        WHERE
            (:#{#req.keyword} IS NULL OR kc.ten LIKE CONCAT('%', :#{#req.keyword}, '%') OR kc.ma_kich_co LIKE CONCAT('%', :#{#req.keyword}, '%'))
            AND kc.deleted = 0
    """, countQuery = """
        SELECT COUNT(kc.id)
        FROM kich_co kc
        WHERE
            (:#{#req.keyword} IS NULL OR kc.ten LIKE CONCAT('%', :#{#req.keyword}, '%') OR kc.ma_kich_co LIKE CONCAT('%', :#{#req.keyword}, '%'))
            AND kc.deleted = 0
    """, nativeQuery = true)
    Page<AdKichCoResponse> getAllKichCos (Pageable pageable, AdFindKichCoRequest req);

    @Query(value = """
    SELECT kc.id AS id,
    kc.ten AS ten
    FROM KichCo kc
    ORDER BY kc.createdDate DESC
""")
    List<AdGetKichCoResponse> getListKichCo ();

    Boolean existsKichCoByMaKichCo(String maKichCo);

    Boolean existsKichCoByTen(String ten);

    @Query("""
        SELECT kc
        FROM KichCo kc
        WHERE kc.ten = :ten AND kc.id != :id
""")
    AdKichCoResponse existsKichCoByTenNotId(@Param("ten") String name, @Param("id") String id);
}
