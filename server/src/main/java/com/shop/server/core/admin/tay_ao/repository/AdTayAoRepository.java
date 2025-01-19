package com.shop.server.core.admin.tay_ao.repository;

import com.shop.server.core.admin.tay_ao.model.request.AdFindTayAoRequest;
import com.shop.server.core.admin.tay_ao.model.response.AdGetTayAoResponse;
import com.shop.server.core.admin.tay_ao.model.response.AdTayAoResponse;
import com.shop.server.repositories.TayAoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdTayAoRepository extends TayAoRepository {

    @Query(value = """
        SELECT 
            ROW_NUMBER() OVER(ORDER BY ta.ngay_tao DESC) AS catalog,
            ta.id as id,
            ta.ma_tay_ao as maTayAo,
            ta.ten AS ten,
            ta.ngay_tao AS ngayTao
        FROM tay_ao ta
        WHERE
            (:#{#req.keyword} IS NULL OR ta.ten LIKE CONCAT('%', :#{#req.keyword}, '%') OR ta.ma_tay_ao LIKE CONCAT('%', :#{#req.keyword}, '%'))
            AND ta.deleted = 0
    """, countQuery = """
        SELECT COUNT(ta.id)
        FROM tay_ao ta
        WHERE
            (:#{#req.keyword} IS NULL OR ta.ten LIKE CONCAT('%', :#{#req.keyword}, '%') OR ta.ma_tay_ao LIKE CONCAT('%', :#{#req.keyword}, '%'))
            AND ta.deleted = 0
    """, nativeQuery = true)
    Page<AdTayAoResponse> getAllTayAos (Pageable pageable, AdFindTayAoRequest req);

    @Query(value = """
    SELECT ta.id AS id,
    ta.ten AS ten
    FROM TayAo ta
    ORDER BY ta.createdDate DESC
""")
    List<AdGetTayAoResponse> getListTayAo ();

    Boolean existsTayAoByMaTayAo(String maTayAo);

    Boolean existsTayAoByTen(String ten);

    @Query("""
        SELECT ta
        FROM TayAo ta
        WHERE ta.ten = :ten AND ta.id != :id
""")
    AdTayAoResponse existsTayAoByTenNotId(@Param("ten") String name, @Param("id") String id);
}
