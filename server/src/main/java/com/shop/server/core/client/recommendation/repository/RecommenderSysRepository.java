package com.shop.server.core.client.recommendation.repository;

import com.shop.server.core.client.recommendation.model.response.RecommenderOrderHistoryResponse;
import com.shop.server.core.client.recommendation.model.response.RecommenderProductResponse;
import com.shop.server.core.client.recommendation.model.response.RecommenderSysResponse;
import com.shop.server.core.client.recommendation.model.response.RecommenderUserResponse;
import com.shop.server.repositories.SanPhamRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecommenderSysRepository extends SanPhamRepository {

    @Query(value = """
            SELECT
                sp.id as id,
                sp.ma_san_pham as maSanPham,
                sp.ten as ten,
                sp.mo_ta as moTa,
                cl.id as idChatLieu,
                cl.ten as tenChatLieu,
                dm.id as idDanhMuc,
                dm.ten as tenDanhMuc,
                ca.id as idCoAo,
                ca.ten as tenCoAo,
                ta.id as idTayAo,
                ta.ten as tenTayAo,
                ht.id  as idHoaTiet,
                ht.ten as tenHoaTiet,
                tn.id as idTinhNang,
                tn.ten as tenTinhNang,
                th.id as idThuongHieu,
                th.ten as tenThuongHieu,
                kd.id as idKieuDang,
                kd.ten as tenKieuDang,
                GROUP_CONCAT(DISTINCT spct.ma_san_pham_chi_tiet) as maSPCTs,
                GROUP_CONCAT(DISTINCT spgg.gia_sau_giam ORDER BY spgg.gia_sau_giam ASC) AS discount,
                GROUP_CONCAT(DISTINCT spct.gia ORDER BY spct.gia ASC) AS gia,
                GROUP_CONCAT(DISTINCT CONCAT(kc.id, ':', kc.ten)) AS kichCos,
                GROUP_CONCAT(DISTINCT CONCAT(ms.id,':' ,ms.ma_mau_sac, ':', ms.ten)) AS colors,
                GROUP_CONCAT(DISTINCT CONCAT(anh.id, ':', anh.url)) AS anhs
            FROM san_pham sp
            JOIN san_pham_chi_tiet spct ON sp.id = spct.id_san_pham
            JOIN danh_muc dm ON dm.id = sp.id_danh_muc
            JOIN chat_lieu cl ON cl.id = spct.id_chat_lieu
            JOIN co_ao ca ON ca.id = spct.id_co_ao
            JOIN tay_ao ta ON ta.id = spct.id_tay_ao
            JOIN hoa_tiet ht ON ht.id = spct.id_hoa_tiet
            JOIN tinh_nang tn ON tn.id = spct.id_tinh_nang
            JOIN thuong_hieu th ON th.id = spct.id_thuong_hieu
            JOIN kieu_dang kd ON kd.id = spct.id_kieu_dang
            JOIN mau_sac ms ON ms.id = spct.id_mau_sac
            JOIN kich_co kc ON kc.id = spct.id_kich_co
            LEFT JOIN anh ON anh.id_san_pham_chi_tiet = spct.id
            LEFT JOIN san_pham_giam_gia spgg ON  spgg.id_san_pham_chi_tiet = spgg.id
            LEFT JOIN dot_giam_gia dgg ON dgg.id = spgg.id_dot_giam_gia
            GROUP BY sp.id, sp.ten ,cl.id, cl.ten,dm.id, dm.ten,ca.id, ca.ten,ta.id, ta.ten,ht.id, ht.ten,tn.id, tn.ten,th.id, th.ten,kd.id, kd.ten, sp.ma_san_pham
            """, nativeQuery = true)
    List<RecommenderSysResponse> getRecommenderSysList();


    @Query(value = """
            SELECT
                kh.id AS id,
                kh.gioi_tinh AS gender,
                (YEAR(CURDATE()) - YEAR(FROM_UNIXTIME(kh.ngay_sinh / 1000))) AS age
            FROM khach_hang kh
            """, nativeQuery = true)
    List<RecommenderUserResponse> getUsers();

    @Query(value = """
            SELECT
                hd.id_khach_hang AS clientId,
                hdct.id_san_pham_chi_tiet AS productVariantId,
                hdct.ngay_tao AS purchaseTimestamp,
                hdct.so_luong AS quantity,
                hdct.gia AS price
            FROM hoa_don_chi_tiet hdct
            LEFT JOIN hoa_don hd ON hd.id = hdct.id_hoa_don
            """, nativeQuery = true)
    List<RecommenderOrderHistoryResponse> getOrderHistories();

    @Query(value = """
            SELECT
                spct.id AS productVariantId,
                sp.id AS id,
                sp.ten AS name,
                dm.ten AS category,
                cl.ten AS material,
                tn.ten AS feature,
                th.ten AS brand,
                kc.ten AS size,
                ms.ten AS color,
                spct.gia AS price
            FROM san_pham_chi_tiet spct
            LEFT JOIN san_pham sp ON sp.id = spct.id_san_pham
            LEFT JOIN danh_muc dm ON sp.id_danh_muc = dm.id
            LEFT JOIN chat_lieu cl ON cl.id = spct.id_chat_lieu
            LEFT JOIN tinh_nang tn ON tn.id = spct.id_tinh_nang
            LEFT JOIN thuong_hieu th ON th.id = spct.id_thuong_hieu
            LEFT JOIN mau_sac ms ON ms.id = spct.id_mau_sac
            LEFT JOIN kich_co kc ON kc.id = spct.id_kich_co
            WHERE spct.deleted = false
            """, nativeQuery = true)
    List<RecommenderProductResponse> getProducts();

}
