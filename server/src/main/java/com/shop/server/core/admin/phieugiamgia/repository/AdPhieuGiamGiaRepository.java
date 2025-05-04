package com.shop.server.core.admin.phieugiamgia.repository;

import com.shop.server.core.admin.phieugiamgia.model.request.PhieuGiamGiaSearchRequest;
import com.shop.server.core.admin.phieugiamgia.model.request.VoucherKhachHangRequest;
import com.shop.server.core.admin.phieugiamgia.model.response.PhieuGiamGiaResponse;
import com.shop.server.entities.main.PhieuGiamGia;
import com.shop.server.repositories.PhieuGiamGiaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

@Repository
@Validated
public interface AdPhieuGiamGiaRepository extends PhieuGiamGiaRepository {

    @Query(value = """
                        SELECT ph.id AS id,
                               ph.ma_phieu_giam_gia AS ma,
                               ph.ten AS ten,
                               ph.so_luong AS soLuong,
                               ph.dieu_kien_giam AS dieuKienGiam,
                               ph.giam_toi_da AS giamToiDa,
                               ph.loai_giam AS loaiGiam,
                               ph.gia_tri_giam as giaTriGiam,
                               ph.ngay_bat_dau AS ngayBatDau,
                               ph.ngay_ket_thuc AS ngayKetThuc,
                                ph.loai_phieu as kieu,
                               ph.trang_thai AS trangThai
                        FROM phieu_giam_gia ph 
                        WHERE ph.id like ?1 
            """, nativeQuery = true)
    Optional<PhieuGiamGiaResponse> getPhieuGiamGia(String id);

    @Query(value = """
                SELECT 
                    ROW_NUMBER() OVER(ORDER BY ph.ngay_tao DESC) AS catalog,
                    ph.id AS id,
                    ph.ma_phieu_giam_gia AS ma,
                    ph.ten AS ten,
                    ph.so_luong AS soLuong,
                    ph.dieu_kien_giam AS dieuKienGiam,
                    ph.giam_toi_da AS giamToiDa,
                    ph.loai_giam AS loaiGiam,
                    ph.gia_tri_giam AS giaTriGiam,
                    ph.ngay_bat_dau AS ngayBatDau,
                    ph.ngay_ket_thuc AS ngayKetThuc,
                    ph.loai_phieu as kieu,
                    CASE
                         WHEN UNIX_TIMESTAMP() * 1000 > ph.ngay_ket_thuc THEN 'EXPRIXED'
                         WHEN ph.trang_thai = 'ACTIVE' THEN
                             CASE
                                 WHEN UNIX_TIMESTAMP() * 1000 BETWEEN ph.ngay_bat_dau AND ph.ngay_ket_thuc THEN 'ACTIVE'
                                 WHEN UNIX_TIMESTAMP() * 1000 < ph.ngay_bat_dau THEN 'NOT_STARTED'
                                 ELSE 'UNKNOWN'
                             END
                         ELSE 'INACTIVE'
                    END AS trangThai
                FROM
                    phieu_giam_gia ph
                WHERE
                    (:#{#request.keyword} IS NULL
                    OR ph.ma_phieu_giam_gia LIKE CONCAT('%',:#{#request.keyword}, '%')
                    OR ph.ten LIKE CONCAT('%',:#{#request.keyword}, '%')
                    )
                    AND (:#{#request.startDate} IS NULL  OR ph.ngay_bat_dau >= :#{#request.startDate})
                    AND (:#{#request.endDate} IS NULL OR ph.ngay_ket_thuc <= :#{#request.endDate})
                    AND (:#{#request.loaiGiam} IS NULL OR ph.loai_giam = :#{#request.loaiGiam})
                    AND (:#{#request.trangThai} IS NULL
                            OR (:#{#request.trangThai} = 'IN_PROGRESS' AND ph.trang_thai = 'ACTIVE' AND (UNIX_TIMESTAMP()*1000 BETWEEN ph.ngay_bat_dau AND ph.ngay_ket_thuc))
                            OR (:#{#request.trangThai} = 'EXPRIXED' AND UNIX_TIMESTAMP()*1000 > ph.ngay_ket_thuc)
                            OR (:#{#request.trangThai} = 'NOT_STARTED' AND ph.trang_thai = 'ACTIVE' AND UNIX_TIMESTAMP()*1000 < ph.ngay_bat_dau)
                            OR ph.trang_thai = :#{#request.trangThai}
                    )
                                                                       
            """, nativeQuery = true)
    Page<PhieuGiamGiaResponse> searchPhieuGiamGiaByKey(PhieuGiamGiaSearchRequest request, Pageable pageable);

    @Query(value = """
                     SELECT *
                            FROM phieu_giam_gia ph
                            WHERE ph.ten = :ten
                              AND (:id IS NULL OR ph.id != :id)
                              
            """, nativeQuery = true)
    PhieuGiamGia existPhieuGiamGiaById(String id,String ten);

    @Query(value = """
                        SELECT ph.id AS id,
                               ph.ma_phieu_giam_gia AS ma,
                               ph.ten AS ten,
                               ph.so_luong AS soLuong,
                               ph.dieu_kien_giam AS dieuKienGiam,
                               ph.giam_toi_da AS giamToiDa,
                               ph.loai_giam AS loaiGiam,
                               ph.gia_tri_giam as giaTriGiam,
                               ph.ngay_bat_dau AS ngayBatDau,
                               ph.ngay_ket_thuc AS ngayKetThuc,
                               ph.trang_thai AS trangThai
                        FROM phieu_giam_gia ph 
                        WHERE ph.ma_phieu_giam_gia like ?1 
            """, nativeQuery = true)
    Optional<PhieuGiamGiaResponse> getPhieuGiamGiaByMa(String ma);

    boolean existsByTen(String ten);

    @Query(value = """
            SELECT pgg.id as id,
                   pgg.ten as ten,
                   pgg.ma_phieu_giam_gia as ma,
                   pgg.so_luong as soLuong,
                   pgg.dieu_kien_giam as dieuKienGiam,
                   pgg.giam_toi_da as giamToiDa,
                   pgg.loai_giam as loaiGiam,
                   pgg.gia_tri_giam as giaTriGiam,
                   pgg.ngay_bat_dau as ngayBatDau,
                   pgg.ngay_ket_thuc as ngayKetThuc,
                   pgg.loai_phieu as kieu,
                    CASE
                         WHEN UNIX_TIMESTAMP() * 1000 > pgg.ngay_ket_thuc THEN 'EXPRIXED'
                         WHEN pgg.trang_thai = 'ACTIVE' THEN
                             CASE
                                 WHEN UNIX_TIMESTAMP() * 1000 BETWEEN pgg.ngay_bat_dau AND pgg.ngay_ket_thuc THEN 'ACTIVE'
                                 WHEN UNIX_TIMESTAMP() * 1000 < pgg.ngay_bat_dau THEN 'NOT_STARTED'
                                 ELSE 'UNKNOWN'
                             END
                         ELSE 'INACTIVE'
                    END AS trangThai           
            FROM 
            phieu_giam_gia pgg JOIN hoa_don hd on pgg.id = hd.id_phieu_giam_gia
            WHERE hd.id_phieu_giam_gia = :id
                        """,nativeQuery = true)
    PhieuGiamGiaResponse checkVoucherInUse(String id);
}
