package com.shop.server.core.admin.billdetail.repository;

import com.shop.server.core.admin.billdetail.model.request.AdminFindBillDetailRequest;
import com.shop.server.core.admin.billdetail.model.response.AdminBillDetailResponse;
import com.shop.server.entities.main.HoaDon;
import com.shop.server.entities.main.HoaDonChiTiet;
import com.shop.server.entities.main.SanPhamChiTiet;
import com.shop.server.repositories.HoaDonChiTietRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdminBillDetailRepository extends HoaDonChiTietRepository {
    @Query(value = """
        SELECT
            ROW_NUMBER() OVER(ORDER BY ct.id DESC) AS catalog,
            ct.id AS id,
            ct.id_hoa_don AS idHoaDon,
            hd.ma_hoa_don AS maHoaDon,
            ct.id_san_pham_chi_tiet AS idSanPhamChiTiet,
            sp.ten AS tenSanPham,
            spct.ten AS tenSanPhamChiTiet,
            anh.url AS anhSanPhamChiTiet,
            ct.so_luong AS soLuong,
            ct.gia AS gia,
            ct.thanh_tien AS thanhTien,
            hd.tong_tien AS tongTienHD,
            hd.tien_giam AS tienGiamHD,
            hd.tien_ship AS tienShip,
            kc.ten AS tenKichCo,
            ms.ten AS tenMau,
            pgg.loai_giam AS loaiGiam,
            pgg.giam_toi_da AS giamToiDa,
            pgg.gia_tri_giam AS giaTriGiam,
            pgg.dieu_kien_giam AS dieuKienGiam,
            pgg.ten AS tenPhieuGiam
        FROM hoa_don_chi_tiet ct
        LEFT JOIN san_pham_chi_tiet spct ON ct.id_san_pham_chi_tiet = spct.id
        JOIN hoa_don hd ON ct.id_hoa_don = hd.id
        JOIN kich_co kc ON spct.id_kich_co = kc.id
        LEFT JOIN phieu_giam_gia pgg ON pgg.id = hd.id_phieu_giam_gia
        LEFT JOIN anh ON spct.id = anh.id_san_pham_chi_tiet
        LEFT JOIN san_pham sp ON spct.id_san_pham = sp.id
        LEFT JOIN mau_sac ms ON spct.id_mau_sac = ms.id
        WHERE
            (:#{#req.keyword} IS NULL OR
             spct.ten LIKE CONCAT('%', :#{#req.keyword}, '%'))
        AND (:#{#req.idHoaDon} IS NULL OR :#{#req.idHoaDon} = hd.id)
    """,countQuery = """
            SELECT
            COUNT(ct.id)
        FROM hoa_don_chi_tiet ct
        LEFT JOIN san_pham_chi_tiet spct ON ct.id_san_pham_chi_tiet = spct.id
        JOIN hoa_don hd ON ct.id_hoa_don = hd.id
        WHERE
            (:#{#req.keyword} IS NULL OR
             spct.ten LIKE CONCAT('%', :#{#req.keyword}, '%'))
        AND (:#{#req.idHoaDon} IS NULL OR :#{#req.idHoaDon} = hd.id)
    """, nativeQuery = true)
    Page<AdminBillDetailResponse> getAdminBillDetailByRequest(Pageable pageable, AdminFindBillDetailRequest req);

    @Query(value = """
        SELECT
            ct.id AS id,
            ct.id_hoa_don AS idHoaDon,
            ct.id_san_pham_chi_tiet AS idSanPhamChiTiet,
            spct.ten AS tenSanPhamChiTiet,
            ct.so_luong AS soLuong,
            ct.gia AS gia,
            ct.thanh_tien AS thanhTien
        FROM hoa_don_chi_tiet ct
        LEFT JOIN san_pham_chi_tiet spct ON ct.id_san_pham_chi_tiet = spct.id
        WHERE ct.id = :id
    """, nativeQuery = true)
    AdminBillDetailResponse getAdminBillDetailById(String id);


    Optional<HoaDonChiTiet> findByHoaDonAndSanPhamChiTiet(HoaDon hoaDon, SanPhamChiTiet sanPhamChiTiet);

}
