package com.shop.server.core.admin.ban_hang.repository;

import com.shop.server.core.admin.ban_hang.model.request.AdminHoaDonKhachHangRequest;
import com.shop.server.core.admin.ban_hang.model.response.AdminVoucherResponse;
import com.shop.server.entities.main.PhieuGiamGia;
import com.shop.server.repositories.PhieuGiamGiaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface AdminPhieuGiamGiaRepository extends PhieuGiamGiaRepository {

    @Query(value = """
    SELECT DISTINCT
        pgg.id AS id,
        pgg.ma_phieu_giam_gia AS ma,
        pgg.ten AS ten,
        pgg.so_luong AS soLuong,
        pgg.dieu_kien_giam AS dieuKienGiam,
        pgg.giam_toi_da AS giamToiDa,
        pgg.loai_giam AS loaiGiam,
        pgg.ngay_bat_dau AS ngayBatDau,
        pgg.ngay_ket_thuc AS ngayKetThuc,
        pgg.loai_phieu as kieu,
        CAST(
            CASE
                WHEN :#{#request.tongTien} IS NULL THEN
                    CASE
                        WHEN pgg.loai_giam = TRUE THEN pgg.gia_tri_giam
                        ELSE pgg.gia_tri_giam
                    END
                WHEN :#{#request.tongTien} >= pgg.dieu_kien_giam THEN
                    CASE
                        WHEN pgg.loai_giam = FALSE THEN
                            LEAST((pgg.gia_tri_giam / 100) * :#{#request.tongTien}, pgg.giam_toi_da)
                        ELSE
                            LEAST(pgg.gia_tri_giam, pgg.giam_toi_da)
                    END
                ELSE 0
            END AS DECIMAL(10,2)
        ) AS giaTriGiam
    FROM phieu_giam_gia pgg
    WHERE
        (:#{#request.keyword} IS NULL
            OR pgg.ma_phieu_giam_gia LIKE CONCAT('%', :#{#request.keyword}, '%')
            OR pgg.ten LIKE CONCAT('%', :#{#request.keyword}, '%')
        )
        AND pgg.ngay_bat_dau <= UNIX_TIMESTAMP() * 1000
        AND pgg.ngay_ket_thuc >= UNIX_TIMESTAMP() * 1000
        AND pgg.trang_thai = 'ACTIVE'
        AND (
            (:#{#request.idKhachHang} IS NULL AND pgg.loai_phieu = false)
                       \s
                        OR (EXISTS (
                                           SELECT 1 FROM khach_hang_phieu_giam_gia khpgg_check
                                           WHERE khpgg_check.id_khach_hang = :#{#request.idKhachHang}
                                           AND khpgg_check.id_phieu_giam_gia = pgg.id
                                       ) OR pgg.loai_phieu = false)
        )
        AND :#{#request.tongTien} >= pgg.dieu_kien_giam
        AND pgg.so_luong > 0
    ORDER BY giaTriGiam DESC
""", nativeQuery = true)
    Page<AdminVoucherResponse> getPhieuGiamGia(AdminHoaDonKhachHangRequest request, Pageable pageable);

    @Query(value = """
    SELECT MIN(pgg.dieu_kien_giam + 0)\s
    FROM phieu_giam_gia pgg
    WHERE (pgg.dieu_kien_giam + 0) > :#{#request.tongTien}
    AND pgg.trang_thai = 'ACTIVE'
    AND pgg.so_luong > 0
    AND ((:#{#request.idKhachHang} IS NULL AND pgg.loai_phieu = false)
                       \s
                        OR (EXISTS (
                                           SELECT 1 FROM khach_hang_phieu_giam_gia khpgg_check
                                           WHERE khpgg_check.id_khach_hang = :#{#request.idKhachHang}
                                           AND khpgg_check.id_phieu_giam_gia = pgg.id
                                       ) OR pgg.loai_phieu = false)
    )
""", nativeQuery = true)
    Optional<BigDecimal> findNextEligibleTongTien(AdminHoaDonKhachHangRequest request);

    @Query(value = """
    SELECT  
            pgg.id AS id,
            pgg.ma_phieu_giam_gia AS ma,
            pgg.ten AS ten,
            pgg.so_luong AS soLuong,
            pgg.gia_tri_giam AS giaTriGiam,
            pgg.loai_giam AS loaiGiam,
            pgg.dieu_kien_giam AS dieuKienGiam,
            CASE
             -- Nếu không có tổng đơn hàng, giữ nguyên giá trị gốc
                    WHEN :#{#request.tongTien} IS NULL THEN
                        CASE
                        WHEN pgg.loai_giam = TRUE THEN pgg.gia_tri_giam  -- Giảm tiền mặt
                                ELSE pgg.gia_tri_giam  -- Giảm phần trăm
                        END
                -- Nếu tổng đơn hàng đạt điều kiện, tính toán giảm giá
                    WHEN :#{#request.tongTien} >= pgg.dieu_kien_giam THEN
                    CASE
                            WHEN pgg.loai_giam = FALSE THEN  -- Giảm theo phần trăm
                                LEAST((pgg.gia_tri_giam / 100) * :#{#request.tongTien}, pgg.giam_toi_da)
                            ELSE  -- Giảm tiền mặt
                                LEAST(pgg.gia_tri_giam, pgg.giam_toi_da)
                    END
                   ELSE 0
             END AS giaTriGiam
             FROM phieu_giam_gia pgg
                    LEFT JOIN khach_hang_phieu_giam_gia khpgg ON pgg.id = khpgg.id_phieu_giam_gia
             WHERE pgg.ngay_bat_dau <= UNIX_TIMESTAMP()*1000
                AND pgg.ngay_ket_thuc >= UNIX_TIMESTAMP()*1000
                AND pgg.trang_thai = 'ACTIVE'
                AND (:#{#request.idKhachHang} IS NULL
                OR khpgg.id_khach_hang = :#{#request.idKhachHang})
                AND pgg.so_luong > 0
                AND pgg.id = (:#{#request.idPhieuGiamGia})
""",nativeQuery = true)
    AdminVoucherResponse getPhieuGiamGiaById(AdminHoaDonKhachHangRequest request);
}
