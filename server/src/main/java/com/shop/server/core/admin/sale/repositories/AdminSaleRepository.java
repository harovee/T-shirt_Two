package com.shop.server.core.admin.sale.repositories;

import com.shop.server.core.admin.sale.models.requests.AdminFindSaleRequest;
import com.shop.server.core.admin.sale.models.requests.AdminSaleProductRequest;
import com.shop.server.core.admin.sale.models.requests.AdminSaleRequest;
import com.shop.server.core.admin.sale.models.responses.AdminDetailSaleResponse;
import com.shop.server.core.admin.sale.models.responses.AdminSaleResponse;
import com.shop.server.repositories.DotGiamGiaRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminSaleRepository extends DotGiamGiaRepository {

    /**
     * Lấy dữ liệu đợt giảm giá dựa trên các trường có trong request
     */
    @Query(value = """
                SELECT
                    ROW_NUMBER() OVER(ORDER BY CASE WHEN :#{#req.orderBy} = 'asc' THEN
                           CASE :#{#req.sortBy}
                               WHEN 'gia_tri' THEN dgg.gia_tri
                               WHEN 'ngay_bat_dau' THEN dgg.ngay_bat_dau
                               WHEN 'ngay_ket_thuc' THEN dgg.ngay_ket_thuc
                           END
                       END ,
                       CASE WHEN :#{#req.orderBy} = 'desc' THEN
                           CASE :#{#req.sortBy}
                               WHEN 'gia_tri' THEN dgg.gia_tri
                               WHEN 'ngay_bat_dau' THEN dgg.ngay_bat_dau
                               WHEN 'ngay_ket_thuc' THEN dgg.ngay_ket_thuc
                           END
                       END DESC
                    ) AS catalog,
                	dgg.id,
                	dgg.ma_dot_giam_gia,
                    dgg.ten,
                    CONCAT(CONVERT(gia_tri, UNSIGNED ),' ', IF(dgg.loai = 'PERCENT', '%', 'Vnd')) AS gia_tri,
                    dgg.ngay_bat_dau,
                    dgg.ngay_ket_thuc,
                    IF(dgg.trang_thai = 'ACTIVE', CASE
                               WHEN UNIX_TIMESTAMP()*1000 BETWEEN dgg.ngay_bat_dau AND dgg.ngay_ket_thuc THEN 'IN_PROGRESS'
                               WHEN UNIX_TIMESTAMP()*1000 > dgg.ngay_ket_thuc THEN 'FINISHED'
                               WHEN UNIX_TIMESTAMP()*1000 < dgg.ngay_bat_dau THEN 'PENDING'
                               ELSE 'UNKNOWN'
                           END, 'INACTIVE') AS trang_thai
                FROM dot_giam_gia dgg
                WHERE
                    dgg.deleted = 0
                AND (:#{#req.loai} IS NULL OR dgg.loai = :#{#req.loai})
                AND (:#{#req.keyword} IS NULL OR
                    dgg.ma_dot_giam_gia LIKE CONCAT('%', :#{#req.keyword}, '%') OR
                    dgg.ten LIKE CONCAT('%', :#{#req.keyword}, '%'))
                AND (:#{#req.trangThai} IS NULL
                    OR (:#{#req.trangThai} = 'IN_PROGRESS' AND (UNIX_TIMESTAMP()*1000 BETWEEN dgg.ngay_bat_dau AND dgg.ngay_ket_thuc))
                    OR (:#{#req.trangThai} = 'FINISHED' AND UNIX_TIMESTAMP()*1000 > dgg.ngay_ket_thuc)
                    OR (:#{#req.trangThai} = 'PENDING' AND UNIX_TIMESTAMP()*1000 < dgg.ngay_bat_dau)
                    OR dgg.trang_thai = :#{#req.trangThai}
                    )
                AND :#{#req.ngayBatDau} IS NULL OR dgg.ngay_bat_dau >= :#{#req.ngayBatDau}
                AND :#{#req.ngayKetThuc} IS NULL OR dgg.ngay_ket_thuc <= :#{#req.ngayKetThuc}
            """, countQuery = """
                SELECT
                    COUNT(dgg.id)
                FROM dot_giam_gia dgg
                WHERE
                    dgg.deleted = false
                AND (:#{#req.loai} IS NULL OR dgg.loai = :#{#req.loai})
                AND (:#{#req.keyword} IS NULL OR
                    dgg.ma_dot_giam_gia LIKE CONCAT('%', :#{#req.keyword}, '%') OR
                    dgg.ten LIKE CONCAT('%', :#{#req.keyword}, '%'))
                AND (:#{#req.trangThai} IS NULL
                    OR (:#{#req.trangThai} = 'IN_PROGRESS' AND (UNIX_TIMESTAMP()*1000 BETWEEN dgg.ngay_bat_dau AND dgg.ngay_ket_thuc))
                    OR (:#{#req.trangThai} = 'FINISHED' AND UNIX_TIMESTAMP()*1000 > dgg.ngay_ket_thuc)
                    OR (:#{#req.trangThai} = 'PENDING' AND UNIX_TIMESTAMP()*1000 < dgg.ngay_bat_dau)
                    OR dgg.trang_thai = :#{#req.trangThai}
                    )
                AND :#{#req.ngayBatDau} IS NULL OR dgg.ngay_bat_dau >= :#{#req.ngayBatDau}
                AND :#{#req.ngayKetThuc} IS NULL OR dgg.ngay_ket_thuc <= :#{#req.ngayKetThuc}
            """, nativeQuery = true)
    Page<AdminSaleResponse> getPromotionByRequest(Pageable pageable, AdminFindSaleRequest req);

    @Query("select dgg from DotGiamGia dgg where dgg.id = ?1")
    Optional<AdminDetailSaleResponse> findDetailSaleResponseById(String id);

    @Modifying
    @Transactional
    @Query(value = """
            update dot_giam_gia dgg
            set dgg.ten = :#{#req.ten},
            dgg.loai = :#{#req.loai},
            dgg.gia_tri = :#{#req.giaTri},
            dgg.gia_tri_giam_toi_da = :#{#req.giaTriGiamToiDa},
            dgg.ngay_bat_dau = :#{#req.ngayBatDau},
            dgg.ngay_ket_thuc = :#{#req.ngayKetThuc},
            dgg.nguoi_sua = :#{#req.nguoiSua},
            dgg.ngay_sua = UNIX_TIMESTAMP()*1000
            where dgg.id = ?1
            """, nativeQuery = true)
    void updateSaleById(String id, AdminSaleRequest req);

    @Modifying
    @Transactional
    @Query("update DotGiamGia dgg set dgg.trangThai = ?2 where dgg.id = ?1")
    void updateSaleStatusById(String id, String status);

    @Modifying
    @Transactional
    @Query("update DotGiamGia dgg set dgg.deleted = true where dgg.id = ?1")
    void deleteSaleById(String id);

    @Modifying
    @Transactional
    @Query(value = """
            INSERT INTO san_pham_giam_gia
            (id, id_san_pham_chi_tiet, id_dot_giam_gia, gia_sau_giam,
            san_pham_giam_gia.deleted, san_pham_giam_gia.ngay_tao, san_pham_giam_gia.nguoi_tao)
            SELECT
                UUID(),
                spct.id,
                :#{#req.idDotGiamGia},
                CASE
                    WHEN :#{#req.loaiGiamGia} = 'PERCENT' THEN spct.gia - (spct.gia * :#{#req.giaTriGiamGia} / 100)
                    WHEN (:#{#req.loaiGiamGia} = 'VND' AND spct.gia >= :#{#req.giaTriGiamGia}) THEN spct.gia - :#{#req.giaTriGiamGia}
                    ELSE spct.gia
                END AS gia_sau_giam,
                false, UNIX_TIMESTAMP()*1000, :#{#req.nhanVien}
            FROM san_pham_chi_tiet spct
            WHERE spct.id IN (:#{#req.idSanPhamChiTiets})
            AND (:#{#req.loaiGiamGia} = 'PERCENT' OR spct.gia >= :#{#req.giaTriGiamGia});
            """, nativeQuery = true)
    void saveSaleProductDetails(AdminSaleProductRequest req);

    boolean existsDotGiamGiaByMaDotGiamGia(String ma);
}
