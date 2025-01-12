package com.shop.server.core.admin.phieugiamgia.repository;

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
""",nativeQuery = true)
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
""",nativeQuery = true)
    Optional<PhieuGiamGiaResponse> getPhieuGiamGia(String id);

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
            WHERE (ph.ma_phieu_giam_gia like ?1 
                    or ph.ten like ?1 
                    or  ph.ngay_tao like ?1
                    or ph.ngay_bat_dau like ?1 
                    or ph.ngay_ket_thuc like ?1
                    or ph.so_luong like ?1
                    or ph.dieu_kien_giam like ?1
                    or ph.giam_toi_da like ?1
                    or ph.loai_giam like ?1
                )
            AND (ph.deleted !=1 or ph.deleted is null)
            order by ph.ngay_tao desc 
""",nativeQuery = true)
    Page<PhieuGiamGiaResponse> searchPhieuGiamGiaByKey(String search,Pageable pageable);

    Boolean existsPhieuGiamGiaByTen(String ten);

    @Query(value = """
         SELECT *
                FROM phieu_giam_gia ph
                WHERE ph.ten = :ten
                  AND (:id IS NULL OR ph.id != :id)
                  AND (ph.deleted != 1 OR ph.deleted IS NULL)
""",nativeQuery = true)
    PhieuGiamGia existPhieuGiamGiaById(String id);
}
