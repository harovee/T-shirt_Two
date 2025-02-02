package com.shop.server.core.admin.anh_san_pham.repository;

import com.shop.server.core.admin.anh_san_pham.model.request.AdFindAnhRequest;
import com.shop.server.core.admin.anh_san_pham.model.response.AdAnhResponse;
import com.shop.server.core.admin.anh_san_pham.model.response.AdGetDanhMucResponse;
import com.shop.server.repositories.AnhRepository;
import com.shop.server.repositories.DanhMucRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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

//    @Query("""
//        SELECT dm
//        FROM DanhMuc dm
//        WHERE dm.ten = :ten AND dm.id != :id
//""")
//    AdAnhResponse existsDanhMucByTenNotId(@Param("ten") String name, @Param("id") String id);
}
