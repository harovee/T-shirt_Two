package com.shop.server.core.admin.mau_sac.repository;

import com.shop.server.core.admin.mau_sac.model.request.AdFindMauSacRequest;
import com.shop.server.core.admin.mau_sac.model.response.AdGetMauSacResponse;
import com.shop.server.core.admin.mau_sac.model.response.AdMauSacResponse;
import com.shop.server.repositories.MauSacRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdMauSacRepository extends MauSacRepository {

    @Query(value = """
        SELECT 
            ROW_NUMBER() OVER(ORDER BY ms.ngay_tao DESC) AS catalog,
            ms.id as id,
            ms.ma_mau_sac as maMauSac,
            ms.ten AS ten,
            ms.ngay_tao AS ngayTao
        FROM mau_sac ms
        WHERE
            (:#{#req.keyword} IS NULL OR ms.ten LIKE CONCAT('%', :#{#req.keyword}, '%') OR ms.ma_mau_sac LIKE CONCAT('%', :#{#req.keyword}, '%'))
            AND ms.deleted = 0
    """, countQuery = """
        SELECT COUNT(ms.id)
        FROM mau_sac ms
        WHERE
            (:#{#req.keyword} IS NULL OR ms.ten LIKE CONCAT('%', :#{#req.keyword}, '%') OR ms.ma_mau_sac LIKE CONCAT('%', :#{#req.keyword}, '%'))
            AND ms.deleted = 0
    """, nativeQuery = true)
    Page<AdMauSacResponse> getAllMauSacs (Pageable pageable, AdFindMauSacRequest req);

    @Query(value = """
    SELECT ms.id AS id,
    ms.ten AS ten,
    ms.maMauSac AS maMauSac
    FROM MauSac ms
    ORDER BY ms.createdDate DESC
""")
    List<AdGetMauSacResponse> getListMauSac ();

    Boolean existsMauSacByMaMauSac(String maMauSac);

    Boolean existsMauSacByTen(String ten);

    @Query("""
        SELECT ms
        FROM MauSac ms
        WHERE ms.ten = :ten AND ms.id != :id
""")
    AdMauSacResponse existsMauSacByTenNotId(@Param("ten") String name, @Param("id") String id);
}
