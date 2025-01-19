package com.shop.server.core.admin.tinh_nang.repository;

import com.shop.server.core.admin.tinh_nang.model.request.AdFindTinhNangRequest;
import com.shop.server.core.admin.tinh_nang.model.response.AdGetTinhNangResponse;
import com.shop.server.core.admin.tinh_nang.model.response.AdTinhNangResponse;
import com.shop.server.repositories.TinhNangRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdTinhNangRepository extends TinhNangRepository {

    @Query(value = """
        SELECT 
            ROW_NUMBER() OVER(ORDER BY tn.ngay_tao DESC) AS catalog,
            tn.id as id,
            tn.ma_tinh_nang as maTinhNang,
            tn.ten AS ten,
            tn.ngay_tao AS ngayTao
        FROM tinh_nang tn
        WHERE
            (:#{#req.keyword} IS NULL OR tn.ten LIKE CONCAT('%', :#{#req.keyword}, '%') OR tn.ma_tinh_nang LIKE CONCAT('%', :#{#req.keyword}, '%'))
            AND tn.deleted = 0
    """, countQuery = """
        SELECT COUNT(tn.id)
        FROM tinh_nang tn
        WHERE
            (:#{#req.keyword} IS NULL OR tn.ten LIKE CONCAT('%', :#{#req.keyword}, '%') OR tn.ma_tinh_nang LIKE CONCAT('%', :#{#req.keyword}, '%'))
            AND tn.deleted = 0
    """, nativeQuery = true)
    Page<AdTinhNangResponse> getAllTinhNangs (Pageable pageable, AdFindTinhNangRequest req);

    @Query(value = """
    SELECT tn.id AS id,
    tn.ten AS ten
    FROM TinhNang tn
    ORDER BY tn.createdDate DESC
""")
    List<AdGetTinhNangResponse> getListTinhNang ();

    Boolean existsTinhNangByMaTinhNang(String maTinhNang);

    Boolean existsTinhNangByTen(String ten);

    @Query("""
        SELECT tn
        FROM TinhNang tn
        WHERE tn.ten = :ten AND tn.id != :id
""")
    AdTinhNangResponse existsTinhNangByTenNotId(@Param("ten") String name, @Param("id") String id);
}
