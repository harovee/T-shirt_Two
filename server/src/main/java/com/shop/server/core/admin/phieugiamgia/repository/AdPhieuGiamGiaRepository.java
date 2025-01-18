package com.shop.server.core.admin.phieugiamgia.repository;

import com.shop.server.core.admin.phieugiamgia.model.request.PhieuGiamGiaSearchRequest;
import com.shop.server.core.admin.phieugiamgia.model.response.PhieuGiamGiaResponse;
import com.shop.server.entities.main.PhieuGiamGia;
import com.shop.server.repositories.PhieuGiamGiaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
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
                               ph.trang_thai AS trangThai
                        FROM phieu_giam_gia ph  
                        where ph.deleted !=1 or ph.deleted is null
                        order by ph.ngay_tao desc
            """, nativeQuery = true)
    Page<PhieuGiamGiaResponse> getAllPhieuGiamGia(Pageable pageable);

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
                        WHERE ph.id like ?1 and (ph.deleted !=1 or ph.deleted is null)
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
                    ph.trang_thai AS trangThai
                FROM
                    phieu_giam_gia ph
                WHERE
                    (:#{#request.keyword} IS NULL
                    OR ph.ma_phieu_giam_gia LIKE CONCAT('%',:#{#request.keyword}, '%')
                    OR ph.ten LIKE CONCAT('%',:#{#request.keyword}, '%')
                    )
                    AND ((:#{#request.startDate} IS NULL AND :#{#request.endDate} IS NULL)
                        OR (ph.ngay_bat_dau >= :#{#request.startDate} AND ph.ngay_ket_thuc <= :#{#request.endDate}))
                    AND (:#{#request.loaiGiam} IS NULL OR ph.loai_giam = :#{#request.loaiGiam})
                    AND (:#{#request.trangThai} is null or ph.trang_thai = :#{#request.trangThai})
                    AND (ph.deleted IS NULL OR ph.deleted = 0)                                                   
            """, nativeQuery = true)
    Page<PhieuGiamGiaResponse> searchPhieuGiamGiaByKey(PhieuGiamGiaSearchRequest request, Pageable pageable);

    @Query(value = """
                     SELECT *
                            FROM phieu_giam_gia ph
                            WHERE ph.ten like :#{#ten}
                              AND (ph.deleted != 1 OR ph.deleted IS NULL)
            """, nativeQuery = true)
    PhieuGiamGia existsPhieuGiamGiaByTen(String ten);

    @Query(value = """
                     SELECT *
                            FROM phieu_giam_gia ph
                            WHERE ph.ten = :ten
                              AND (:id IS NULL OR ph.id != :id)
                              AND (ph.deleted != 1 OR ph.deleted IS NULL)
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
                        WHERE ph.ma_phieu_giam_gia like ?1 and (ph.deleted !=1 or ph.deleted is null)
            """, nativeQuery = true)
    Optional<PhieuGiamGiaResponse> getPhieuGiamGiaByMa(String ma);
}
