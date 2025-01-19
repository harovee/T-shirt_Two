package com.shop.server.core.admin.co_ao.repository;

import com.shop.server.core.admin.co_ao.model.request.AdFindCoAoRequest;
import com.shop.server.core.admin.co_ao.model.response.AdCoAoResponse;
import com.shop.server.core.admin.co_ao.model.response.AdGetCoAoResponse;
import com.shop.server.repositories.CoAoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdCoAoRepository extends CoAoRepository {

    @Query(value = """
        SELECT 
            ROW_NUMBER() OVER(ORDER BY ca.ngay_tao DESC) AS catalog,
            ca.id as id,
            ca.ma_co_ao as maCoAo,
            ca.ten AS ten,
            ca.ngay_tao AS ngayTao
        FROM co_ao ca
        WHERE
            (:#{#req.keyword} IS NULL OR ca.ten LIKE CONCAT('%', :#{#req.keyword}, '%') OR ca.ma_co_ao LIKE CONCAT('%', :#{#req.keyword}, '%'))
            AND ca.deleted = 0
    """, countQuery = """
        SELECT COUNT(ca.id)
        FROM co_ao ca
        WHERE
            (:#{#req.keyword} IS NULL OR ca.ten LIKE CONCAT('%', :#{#req.keyword}, '%') OR ca.ma_co_ao LIKE CONCAT('%', :#{#req.keyword}, '%'))
            AND ca.deleted = 0
    """, nativeQuery = true)
    Page<AdCoAoResponse> getAllCoAos (Pageable pageable, AdFindCoAoRequest req);

    @Query(value = """
    SELECT ca.id AS id,
    ca.ten AS ten
    FROM CoAo ca
    ORDER BY ca.createdDate DESC
""")
    List<AdGetCoAoResponse> getListCoAo ();

    Boolean existsCoAoByMaCoAo(String maCoAo);

    Boolean existsCoAoByTen(String ten);

    @Query("""
        SELECT ca
        FROM CoAo ca
        WHERE ca.ten = :ten AND ca.id != :id
""")
    AdCoAoResponse existsCoAoByTenNotId(@Param("ten") String name, @Param("id") String id);
}
