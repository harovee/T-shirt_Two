package com.shop.server.core.admin.san_pham_chi_tiet.repository;

import com.shop.server.core.admin.san_pham_chi_tiet.model.request.AdFindSpctRequest;
import com.shop.server.core.admin.san_pham_chi_tiet.model.response.AdSanPhamChiTietResponse;
import com.shop.server.repositories.SanPhamChiTietRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdSanPhamChiTietRepository extends SanPhamChiTietRepository {

    @Query(value = """
                SELECT
        			ROW_NUMBER() OVER(ORDER BY spct.ngay_tao DESC) AS catalog,
                    spct.id as id,
                    spct.ma_san_pham_chi_tiet as maSanPhamChitiet,
                    cl.ten AS chatLieu,
                    ca.ten AS coAo,
                    ht.ten AS hoaTiet,
                    kc.ten AS kichCo,
                    kd.ten AS kieuDang,
                    ms.ten AS mauSac,
                    ta.ten AS tayAo,
                    th.ten AS thuongHieu,
                    tn.ten AS tinhNang,
                    sp.ten AS sanPham,
                    spct.gia AS gia,
                    spct.so_luong AS soLuong,
                    spct.trang_thai AS trangThai
                FROM san_pham_chi_tiet spct
                    LEFT JOIN chat_lieu cl ON spct.id_chat_lieu = cl.id
                    LEFT JOIN co_ao ca ON spct.id_co_ao = ca.id
                    LEFT JOIN hoa_tiet ht ON spct.id_hoa_tiet = ht.id
                    LEFT JOIN kich_co kc ON spct.id_kich_co = kc.id
                    LEFT JOIN kieu_dang kd ON spct.id_kieu_dang = kd.id
                    LEFT JOIN mau_sac ms ON spct.id_mau_sac = ms.id
                    LEFT JOIN tay_ao ta ON spct.id_tay_ao = ta.id
                    LEFT JOIN thuong_hieu th ON spct.id_thuong_hieu = th.id
                    LEFT JOIN tinh_nang tn ON spct.id_tinh_nang = tn.id
                    JOIN san_pham sp ON spct.id_san_pham = sp.id
        		WHERE spct.id_san_pham = :#{#req.idSanPham}
                    AND (:#{#req.keyword} IS NULL OR spct.ma_san_pham_chi_tiet LIKE CONCAT('%', :#{#req.keyword}, '%'))
                    AND (:#{#req.idChatLieu} IS NULL OR spct.id_chat_lieu LIKE :#{#req.idChatLieu})
                    AND (:#{#req.idCoAo} IS NULL OR spct.id_co_ao LIKE :#{#req.idCoAo})
                    AND (:#{#req.idHoaTiet} IS NULL OR spct.id_hoa_tiet LIKE :#{#req.idHoaTiet})
                    AND (:#{#req.idKichCo} IS NULL OR spct.id_kich_co LIKE :#{#req.idKichCo})
                    AND (:#{#req.idKieuDang} IS NULL OR spct.id_kieu_dang LIKE :#{#req.idKieuDang})
                    AND (:#{#req.idMauSac} IS NULL OR spct.id_mau_sac LIKE :#{#req.idMauSac})
                    AND (:#{#req.idTayAo} IS NULL OR spct.id_tay_ao LIKE :#{#req.idTayAo})
                    AND (:#{#req.idThuongHieu} IS NULL OR spct.id_thuong_hieu LIKE :#{#req.idThuongHieu})
                    AND (:#{#req.idTinhNang} IS NULL OR spct.id_tinh_nang LIKE :#{#req.idTinhNang})
            		AND (:#{#req.trangThai} IS NULL OR spct.trang_thai = :#{#req.trangThai})
            		AND (:#{#req.gia} IS NULL OR spct.gia <= :#{#req.gia})
                    AND spct.deleted = 0
    """, countQuery = """
        SELECT COUNT(spct.id)
        FROM san_pham_chi_tiet spct
                JOIN san_pham sp ON spct.id_san_pham = sp.id
                JOIN danh_muc dm ON sp.id_danh_muc = dm.id
            WHERE spct.id_san_pham = :#{#req.idSanPham}
                AND (:#{#req.keyword} IS NULL OR spct.ma_san_pham_chi_tiet LIKE CONCAT('%', :#{#req.keyword}, '%'))
                AND (:#{#req.idChatLieu} IS NULL OR spct.id_chat_lieu = :#{#req.idChatLieu})
                AND (:#{#req.idCoAo} IS NULL OR spct.id_co_ao = :#{#req.idCoAo})
                AND (:#{#req.idHoaTiet} IS NULL OR spct.id_hoa_tiet = :#{#req.idHoaTiet})
                AND (:#{#req.idKichCo} IS NULL OR spct.id_kich_co = :#{#req.idKichCo})
                AND (:#{#req.idKieuDang} IS NULL OR spct.id_kieu_dang = :#{#req.idKieuDang})
                AND (:#{#req.idMauSac} IS NULL OR spct.id_mau_sac = :#{#req.idMauSac})
                AND (:#{#req.idTayAo} IS NULL OR spct.id_tay_ao = :#{#req.idTayAo})
                AND (:#{#req.idThuongHieu} IS NULL OR spct.id_thuong_hieu = :#{#req.idThuongHieu})
                AND (:#{#req.idTinhNang} IS NULL OR spct.id_tinh_nang = :#{#req.idTinhNang})
                AND (:#{#req.trangThai} IS NULL OR spct.trang_thai = :#{#req.trangThai})
                AND (:#{#req.gia} IS NULL OR spct.gia <= :#{#req.gia})
                AND spct.deleted = 0
    """, nativeQuery = true)
    Page<AdSanPhamChiTietResponse> getAllSanPhamChiTietsByIdSanPham(Pageable pageable, AdFindSpctRequest req);

    @Query(value = """
                SELECT
        			ROW_NUMBER() OVER(ORDER BY spct.ngay_tao DESC) AS catalog,
                    spct.id as id,
                    spct.ma_san_pham_chi_tiet as maSanPhamChitiet,
                    cl.ten AS chatLieu,
                    ca.ten AS coAo,
                    ht.ten AS hoaTiet,
                    kc.ten AS kichCo,
                    kd.ten AS kieuDang,
                    ms.ten AS mauSac,
                    ta.ten AS tayAo,
                    th.ten AS thuongHieu,
                    tn.ten AS tinhNang,
                    sp.ten AS sanPham,
                    spct.gia AS gia,
                    spct.so_luong AS soLuong,
                    spct.trang_thai AS trangThai
                FROM san_pham_chi_tiet spct
                    LEFT JOIN chat_lieu cl ON spct.id_chat_lieu = cl.id
                    LEFT JOIN co_ao ca ON spct.id_co_ao = ca.id
                    LEFT JOIN hoa_tiet ht ON spct.id_hoa_tiet = ht.id
                    LEFT JOIN kich_co kc ON spct.id_kich_co = kc.id
                    LEFT JOIN kieu_dang kd ON spct.id_kieu_dang = kd.id
                    LEFT JOIN mau_sac ms ON spct.id_mau_sac = ms.id
                    LEFT JOIN tay_ao ta ON spct.id_tay_ao = ta.id
                    LEFT JOIN thuong_hieu th ON spct.id_thuong_hieu = th.id
                    LEFT JOIN tinh_nang tn ON spct.id_tinh_nang = tn.id
                    JOIN san_pham sp ON spct.id_san_pham = sp.id
        		WHERE (:#{#req.keyword} IS NULL OR spct.ma_san_pham_chi_tiet LIKE CONCAT('%', :#{#req.keyword}, '%'))
                    AND (:#{#req.idChatLieu} IS NULL OR spct.id_chat_lieu LIKE :#{#req.idChatLieu})
                    AND (:#{#req.idCoAo} IS NULL OR spct.id_co_ao LIKE :#{#req.idCoAo})
                    AND (:#{#req.idHoaTiet} IS NULL OR spct.id_hoa_tiet LIKE :#{#req.idHoaTiet})
                    AND (:#{#req.idKichCo} IS NULL OR spct.id_kich_co LIKE :#{#req.idKichCo})
                    AND (:#{#req.idKieuDang} IS NULL OR spct.id_kieu_dang LIKE :#{#req.idKieuDang})
                    AND (:#{#req.idMauSac} IS NULL OR spct.id_mau_sac LIKE :#{#req.idMauSac})
                    AND (:#{#req.idTayAo} IS NULL OR spct.id_tay_ao LIKE :#{#req.idTayAo})
                    AND (:#{#req.idThuongHieu} IS NULL OR spct.id_thuong_hieu LIKE :#{#req.idThuongHieu})
                    AND (:#{#req.idTinhNang} IS NULL OR spct.id_tinh_nang LIKE :#{#req.idTinhNang})
            		AND (:#{#req.trangThai} IS NULL OR spct.trang_thai = :#{#req.trangThai})
            		AND (:#{#req.gia} IS NULL OR spct.gia <= :#{#req.gia})
                    AND spct.deleted = 0
    """, countQuery = """
        SELECT COUNT(spct.id)
        FROM san_pham_chi_tiet spct
            WHERE (:#{#req.keyword} IS NULL OR spct.ma_san_pham_chi_tiet LIKE CONCAT('%', :#{#req.keyword}, '%'))
                AND (:#{#req.idChatLieu} IS NULL OR spct.id_chat_lieu = :#{#req.idChatLieu})
                AND (:#{#req.idCoAo} IS NULL OR spct.id_co_ao = :#{#req.idCoAo})
                AND (:#{#req.idHoaTiet} IS NULL OR spct.id_hoa_tiet = :#{#req.idHoaTiet})
                AND (:#{#req.idKichCo} IS NULL OR spct.id_kich_co = :#{#req.idKichCo})
                AND (:#{#req.idKieuDang} IS NULL OR spct.id_kieu_dang = :#{#req.idKieuDang})
                AND (:#{#req.idMauSac} IS NULL OR spct.id_mau_sac = :#{#req.idMauSac})
                AND (:#{#req.idTayAo} IS NULL OR spct.id_tay_ao = :#{#req.idTayAo})
                AND (:#{#req.idThuongHieu} IS NULL OR spct.id_thuong_hieu = :#{#req.idThuongHieu})
                AND (:#{#req.idTinhNang} IS NULL OR spct.id_tinh_nang = :#{#req.idTinhNang})
                AND (:#{#req.trangThai} IS NULL OR spct.trang_thai = :#{#req.trangThai})
                AND (:#{#req.gia} IS NULL OR spct.gia <= :#{#req.gia})
                AND spct.deleted = 0
    """, nativeQuery = true)
    Page<AdSanPhamChiTietResponse> getAllSanPhamChiTiets(Pageable pageable, AdFindSpctRequest req);

    @Query(value = """
                SELECT
        			ROW_NUMBER() OVER(ORDER BY spct.ngay_tao DESC) AS catalog,
                    spct.id as id,
                    spct.ma_san_pham_chi_tiet as maSanPhamChitiet,
                    cl.ten AS chatLieu,
                    ca.ten AS coAo,
                    ht.ten AS hoaTiet,
                    kc.ten AS kichCo,
                    kd.ten AS kieuDang,
                    ms.ten AS mauSac,
                    ta.ten AS tayAo,
                    th.ten AS thuongHieu,
                    tn.ten AS tinhNang,
                    sp.ten AS sanPham,
                    spct.gia AS gia,
                    spct.so_luong AS soLuong,
                    spct.trang_thai AS trangThai
                FROM san_pham_chi_tiet spct
                    LEFT JOIN chat_lieu cl ON spct.id_chat_lieu = cl.id
                    LEFT JOIN co_ao ca ON spct.id_co_ao = ca.id
                    LEFT JOIN hoa_tiet ht ON spct.id_hoa_tiet = ht.id
                    LEFT JOIN kich_co kc ON spct.id_kich_co = kc.id
                    LEFT JOIN kieu_dang kd ON spct.id_kieu_dang = kd.id
                    LEFT JOIN mau_sac ms ON spct.id_mau_sac = ms.id
                    LEFT JOIN tay_ao ta ON spct.id_tay_ao = ta.id
                    LEFT JOIN thuong_hieu th ON spct.id_thuong_hieu = th.id
                    LEFT JOIN tinh_nang tn ON spct.id_tinh_nang = tn.id
                    JOIN san_pham sp ON spct.id_san_pham = sp.id
        		WHERE (:#{#req.keyword} IS NULL OR spct.ma_san_pham_chi_tiet LIKE CONCAT('%', :#{#req.keyword}, '%'))
                    AND (:#{#req.idChatLieu} IS NULL OR spct.id_chat_lieu LIKE :#{#req.idChatLieu})
                    AND (:#{#req.idCoAo} IS NULL OR spct.id_co_ao LIKE :#{#req.idCoAo})
                    AND (:#{#req.idHoaTiet} IS NULL OR spct.id_hoa_tiet LIKE :#{#req.idHoaTiet})
                    AND (:#{#req.idKichCo} IS NULL OR spct.id_kich_co LIKE :#{#req.idKichCo})
                    AND (:#{#req.idKieuDang} IS NULL OR spct.id_kieu_dang LIKE :#{#req.idKieuDang})
                    AND (:#{#req.idMauSac} IS NULL OR spct.id_mau_sac LIKE :#{#req.idMauSac})
                    AND (:#{#req.idTayAo} IS NULL OR spct.id_tay_ao LIKE :#{#req.idTayAo})
                    AND (:#{#req.idThuongHieu} IS NULL OR spct.id_thuong_hieu LIKE :#{#req.idThuongHieu})
                    AND (:#{#req.idTinhNang} IS NULL OR spct.id_tinh_nang LIKE :#{#req.idTinhNang})
            		AND (:#{#req.trangThai} IS NULL OR spct.trang_thai = :#{#req.trangThai})
            		AND (:#{#req.gia} IS NULL OR spct.gia <= :#{#req.gia})
                    AND spct.deleted = 0
    """, countQuery = """
        SELECT COUNT(spct.id)
        FROM san_pham_chi_tiet spct
            WHERE (:#{#req.keyword} IS NULL OR spct.ma_san_pham_chi_tiet LIKE CONCAT('%', :#{#req.keyword}, '%'))
                AND (:#{#req.idChatLieu} IS NULL OR spct.id_chat_lieu = :#{#req.idChatLieu})
                AND (:#{#req.idCoAo} IS NULL OR spct.id_co_ao = :#{#req.idCoAo})
                AND (:#{#req.idHoaTiet} IS NULL OR spct.id_hoa_tiet = :#{#req.idHoaTiet})
                AND (:#{#req.idKichCo} IS NULL OR spct.id_kich_co = :#{#req.idKichCo})
                AND (:#{#req.idKieuDang} IS NULL OR spct.id_kieu_dang = :#{#req.idKieuDang})
                AND (:#{#req.idMauSac} IS NULL OR spct.id_mau_sac = :#{#req.idMauSac})
                AND (:#{#req.idTayAo} IS NULL OR spct.id_tay_ao = :#{#req.idTayAo})
                AND (:#{#req.idThuongHieu} IS NULL OR spct.id_thuong_hieu = :#{#req.idThuongHieu})
                AND (:#{#req.idTinhNang} IS NULL OR spct.id_tinh_nang = :#{#req.idTinhNang})
                AND (:#{#req.trangThai} IS NULL OR spct.trang_thai = :#{#req.trangThai})
                AND (:#{#req.gia} IS NULL OR spct.gia <= :#{#req.gia})
                AND spct.deleted = 0
    """, nativeQuery = true)
    List<AdSanPhamChiTietResponse> getListAllSanPhamChiTiet(AdFindSpctRequest req);

    @Query(value = """
                SELECT
        			ROW_NUMBER() OVER(ORDER BY spct.ngay_tao DESC) AS catalog,
                    spct.id as id,
                    spct.ma_san_pham_chi_tiet as maSanPhamChitiet,
                    cl.ten AS chatLieu,
                    ca.ten AS coAo,
                    ht.ten AS hoaTiet,
                    kc.ten AS kichCo,
                    kd.ten AS kieuDang,
                    ms.ten AS mauSac,
                    ta.ten AS tayAo,
                    th.ten AS thuongHieu,
                    tn.ten AS tinhNang,
                    sp.ten AS sanPham,
                    spct.gia AS gia,
                    spct.so_luong AS soLuong,
                    spct.trang_thai AS trangThai
                FROM san_pham_chi_tiet spct
                    LEFT JOIN chat_lieu cl ON spct.id_chat_lieu = cl.id
                    LEFT JOIN co_ao ca ON spct.id_co_ao = ca.id
                    LEFT JOIN hoa_tiet ht ON spct.id_hoa_tiet = ht.id
                    LEFT JOIN kich_co kc ON spct.id_kich_co = kc.id
                    LEFT JOIN kieu_dang kd ON spct.id_kieu_dang = kd.id
                    LEFT JOIN mau_sac ms ON spct.id_mau_sac = ms.id
                    LEFT JOIN tay_ao ta ON spct.id_tay_ao = ta.id
                    LEFT JOIN thuong_hieu th ON spct.id_thuong_hieu = th.id
                    LEFT JOIN tinh_nang tn ON spct.id_tinh_nang = tn.id
                    JOIN san_pham sp ON spct.id_san_pham = sp.id
        		WHERE spct.id_san_pham = :#{#req.idSanPham}
                    AND (:#{#req.keyword} IS NULL OR spct.ma_san_pham_chi_tiet LIKE CONCAT('%', :#{#req.keyword}, '%'))
                    AND (:#{#req.idChatLieu} IS NULL OR spct.id_chat_lieu LIKE :#{#req.idChatLieu})
                    AND (:#{#req.idCoAo} IS NULL OR spct.id_co_ao LIKE :#{#req.idCoAo})
                    AND (:#{#req.idHoaTiet} IS NULL OR spct.id_hoa_tiet LIKE :#{#req.idHoaTiet})
                    AND (:#{#req.idKichCo} IS NULL OR spct.id_kich_co LIKE :#{#req.idKichCo})
                    AND (:#{#req.idKieuDang} IS NULL OR spct.id_kieu_dang LIKE :#{#req.idKieuDang})
                    AND (:#{#req.idMauSac} IS NULL OR spct.id_mau_sac LIKE :#{#req.idMauSac})
                    AND (:#{#req.idTayAo} IS NULL OR spct.id_tay_ao LIKE :#{#req.idTayAo})
                    AND (:#{#req.idThuongHieu} IS NULL OR spct.id_thuong_hieu LIKE :#{#req.idThuongHieu})
                    AND (:#{#req.idTinhNang} IS NULL OR spct.id_tinh_nang LIKE :#{#req.idTinhNang})
            		AND (:#{#req.trangThai} IS NULL OR spct.trang_thai = :#{#req.trangThai})
            		AND (:#{#req.gia} IS NULL OR spct.gia <= :#{#req.gia})
                    AND spct.deleted = 0
    """, countQuery = """
        SELECT COUNT(spct.id)
        FROM san_pham_chi_tiet spct
                JOIN san_pham sp ON spct.id_san_pham = sp.id
                JOIN danh_muc dm ON sp.id_danh_muc = dm.id
            WHERE spct.id_san_pham = :#{#req.idSanPham}
                AND (:#{#req.keyword} IS NULL OR spct.ma_san_pham_chi_tiet LIKE CONCAT('%', :#{#req.keyword}, '%'))
                AND (:#{#req.idChatLieu} IS NULL OR spct.id_chat_lieu = :#{#req.idChatLieu})
                AND (:#{#req.idCoAo} IS NULL OR spct.id_co_ao = :#{#req.idCoAo})
                AND (:#{#req.idHoaTiet} IS NULL OR spct.id_hoa_tiet = :#{#req.idHoaTiet})
                AND (:#{#req.idKichCo} IS NULL OR spct.id_kich_co = :#{#req.idKichCo})
                AND (:#{#req.idKieuDang} IS NULL OR spct.id_kieu_dang = :#{#req.idKieuDang})
                AND (:#{#req.idMauSac} IS NULL OR spct.id_mau_sac = :#{#req.idMauSac})
                AND (:#{#req.idTayAo} IS NULL OR spct.id_tay_ao = :#{#req.idTayAo})
                AND (:#{#req.idThuongHieu} IS NULL OR spct.id_thuong_hieu = :#{#req.idThuongHieu})
                AND (:#{#req.idTinhNang} IS NULL OR spct.id_tinh_nang = :#{#req.idTinhNang})
                AND (:#{#req.trangThai} IS NULL OR spct.trang_thai = :#{#req.trangThai})
                AND (:#{#req.gia} IS NULL OR spct.gia <= :#{#req.gia})
                AND spct.deleted = 0
    """, nativeQuery = true)
    List<AdSanPhamChiTietResponse> getListAllSanPhamChiTietsByIdSanPham (AdFindSpctRequest req);

    @Query(value = """
                SELECT
        			ROW_NUMBER() OVER(ORDER BY spct.ngay_tao DESC) AS catalog,
                    spct.id as id,
                    spct.ma_san_pham_chi_tiet as maSanPhamChitiet,
                    cl.id AS chatLieu,
                    ca.id AS coAo,
                    ht.id AS hoaTiet,
                    kc.id AS kichCo,
                    kd.id AS kieuDang,
                    ms.id AS mauSac,
                    ta.id AS tayAo,
                    th.id AS thuongHieu,
                    tn.id AS tinhNang,
                    sp.id AS sanPham,
                    spct.gia AS gia,
                    spct.so_luong AS soLuong,
                    spct.trang_thai AS trangThai
                FROM san_pham_chi_tiet spct
                    LEFT JOIN chat_lieu cl ON spct.id_chat_lieu = cl.id
                    LEFT JOIN co_ao ca ON spct.id_co_ao = ca.id
                    LEFT JOIN hoa_tiet ht ON spct.id_hoa_tiet = ht.id
                    LEFT JOIN kich_co kc ON spct.id_kich_co = kc.id
                    LEFT JOIN kieu_dang kd ON spct.id_kieu_dang = kd.id
                    LEFT JOIN mau_sac ms ON spct.id_mau_sac = ms.id
                    LEFT JOIN tay_ao ta ON spct.id_tay_ao = ta.id
                    LEFT JOIN thuong_hieu th ON spct.id_thuong_hieu = th.id
                    LEFT JOIN tinh_nang tn ON spct.id_tinh_nang = tn.id
                    JOIN san_pham sp ON spct.id_san_pham = sp.id
        		WHERE spct.deleted = 0
    """, nativeQuery = true)
    List<AdSanPhamChiTietResponse> getListSanPhamChiTiets ();

    Boolean existsSanPhamChiTietByMaSPCT(String maSPCT);
}
