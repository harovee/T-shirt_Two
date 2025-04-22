package com.shop.server.core.admin.image.repository;

import com.shop.server.core.admin.image.model.request.AdFindAnhRequest;
import com.shop.server.core.admin.image.model.response.AdAnhResponse;
import com.shop.server.repositories.AnhRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface AdAnhRepository extends AnhRepository {

    @Query(value = """
        SELECT 
            ROW_NUMBER() OVER(ORDER BY a.ngay_tao DESC) AS catalog,
            a.id as id,
            a.ten as ten,
            a.url AS url
        FROM anh a
        WHERE
            (a.id_san_pham_chi_tiet = :#{#req.idSanPhamChiTiet})
            AND a.deleted = 0
    """, nativeQuery = true)
    List<AdAnhResponse> getAllAnh (AdFindAnhRequest req);

    Boolean existsAnhByUrl(String url);

    @Modifying
    @Transactional
    @Query(value = """
            DELETE FROM anh WHERE anh.id_san_pham_chi_tiet = :idSanPhamChiTiet 
    """, nativeQuery = true)
    void deleteAllByIdSanPhamChiTiet(String idSanPhamChiTiet);

//    @Query("""
//        SELECT dm
//        FROM DanhMuc dm
//        WHERE dm.ten = :ten AND dm.id != :id
//""")
//    AdAnhResponse existsDanhMucByTenNotId(@Param("ten") String name, @Param("id") String id);
}
