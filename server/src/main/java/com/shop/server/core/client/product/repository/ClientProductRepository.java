package com.shop.server.core.client.product.repository;

import com.shop.server.core.client.product.model.request.ClientProductDetailRequest;
import com.shop.server.core.client.product.model.request.ClientProductRequest;
import com.shop.server.core.client.product.model.request.ClientProductSearchRequest;
import com.shop.server.core.client.product.model.response.ClientProductDetailResponse;
import com.shop.server.core.client.product.model.response.ClientProductProjectionResponse;
import com.shop.server.repositories.SanPhamRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientProductRepository extends SanPhamRepository {

    @Query(value = """
                  SELECT  ROW_NUMBER() OVER(ORDER BY MAX(spct.ngay_tao) DESC) AS catalog,
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
                          ht.id as idHoaTiet,
                          ht.ten as tenHoaTiet,
                          tn.id as idTinhNang,
                          tn.ten as tenTinhNang,
                          th.id as idThuongHieu,
                          th.ten as tenThuongHieu,
                          kd.id as idKieuDang,
                          kd.ten as tenKieuDang,
                          spct.gioi_tinh as gioiTinh,
                          GROUP_CONCAT(DISTINCT spct.id) as maSPCTs,
                          GROUP_CONCAT(DISTINCT best_discounts.gia_sau_giam ORDER BY best_discounts.gia_sau_giam ASC) AS discount,
                          GROUP_CONCAT(DISTINCT spct.gia ORDER BY spct.gia ASC) AS gia,
                          GROUP_CONCAT(DISTINCT
                              CASE
                                  WHEN best_discounts.gia_sau_giam IS NOT NULL
                                  THEN CONCAT(ROUND((spct.gia - best_discounts.gia_sau_giam) / spct.gia * 100, 1), '%')
                                  ELSE NULL
                                  END
                                  ORDER BY (CASE WHEN best_discounts.gia_sau_giam IS NOT NULL
                                  THEN (spct.gia - best_discounts.gia_sau_giam) / spct.gia
                                  ELSE 0 END) DESC
                              ) AS phanTramGiam,
                          GROUP_CONCAT(DISTINCT CONCAT(kc.id, ':', kc.ten)) AS kichCos,
                          GROUP_CONCAT(DISTINCT CONCAT(ms.id,':' ,ms.ma_mau_sac, ':', ms.ten)) AS colors,
                          GROUP_CONCAT(DISTINCT anh.url) AS anhs
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
                  LEFT JOIN (
                      SELECT
                         spgg.id_san_pham_chi_tiet,
                         spgg.gia_sau_giam,
                         spgg.id_dot_giam_gia,
                         ROW_NUMBER() OVER (
                               PARTITION BY spgg.id_san_pham_chi_tiet
                                  ORDER BY (spct_inner.gia - spgg.gia_sau_giam) / spct_inner.gia DESC) as rank_num
                      FROM san_pham_giam_gia spgg
                      JOIN dot_giam_gia dgg ON dgg.id = spgg.id_dot_giam_gia
                      JOIN san_pham_chi_tiet spct_inner ON spct_inner.id = spgg.id_san_pham_chi_tiet
                      WHERE dgg.ngay_bat_dau <= UNIX_TIMESTAMP() * 1000
                      AND dgg.ngay_ket_thuc >= UNIX_TIMESTAMP() * 1000
                      AND dgg.trang_thai = 'ACTIVE'
                      AND dgg.deleted = 0
                      ) best_discounts ON best_discounts.id_san_pham_chi_tiet = spct.id AND best_discounts.rank_num = 1
                  LEFT JOIN dot_giam_gia dgg ON dgg.id = best_discounts.id_dot_giam_gia
                  WHERE sp.trang_thai = 0
                  AND spct.so_luong > 0
                  AND spct.trang_thai = 0
                  AND spct.so_luong IS NOT NULL
                  AND (spct.gia <= :#{#request.max} OR :#{#request.max} IS NULL)
                  AND (spct.gia >= :#{#request.min} OR :#{#request.min} IS NULL)
                  AND (:#{#request.tenSanPham} IS NULL OR sp.ten LIKE CONCAT('%', :#{#request.tenSanPham}, '%'))
                  AND (:#{#request.tenDanhMuc} IS NULL OR dm.ten LIKE CONCAT('%', :#{#request.tenDanhMuc}, '%'))
                  AND (:#{#request.tenChatLieu} IS NULL OR cl.ten LIKE CONCAT('%', :#{#request.tenChatLieu}, '%'))
                  AND (:#{#request.tenKieuDang} IS NULL OR kd.ten LIKE CONCAT('%', :#{#request.tenKieuDang}, '%'))
                  AND (:#{#request.tenThuongHieu} IS NULL OR th.ten LIKE CONCAT('%', :#{#request.tenThuongHieu}, '%'))
                  GROUP BY sp.id, sp.ten, cl.id, cl.ten, dm.id, dm.ten, ca.id, ca.ten, ta.id, ta.ten,   
                           ht.id, ht.ten, tn.id, tn.ten, th.id, th.ten, kd.id, kd.ten, sp.ma_san_pham, sp.mo_ta,spct.gioi_tinh
            """,
            countQuery = """
              SELECT COUNT( sp.id) 
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
              LEFT JOIN (
                  SELECT
                     spgg.id_san_pham_chi_tiet,
                     spgg.gia_sau_giam,
                     spgg.id_dot_giam_gia
                  FROM san_pham_giam_gia spgg
                  JOIN dot_giam_gia dgg ON dgg.id = spgg.id_dot_giam_gia
                  JOIN san_pham_chi_tiet spct_inner ON spct_inner.id = spgg.id_san_pham_chi_tiet
                  WHERE dgg.ngay_bat_dau <= UNIX_TIMESTAMP() * 1000
                  AND dgg.ngay_ket_thuc >= UNIX_TIMESTAMP() * 1000
                  AND dgg.trang_thai = 'ACTIVE'
                  AND dgg.deleted = 0
              ) best_discounts ON best_discounts.id_san_pham_chi_tiet = spct.id
              WHERE sp.trang_thai = 0
              AND spct.so_luong > 0
              AND spct.trang_thai = 0
              AND spct.so_luong IS NOT NULL
              AND (spct.gia <= :#{#request.max} OR :#{#request.max} IS NULL)
              AND (spct.gia >= :#{#request.min} OR :#{#request.min} IS NULL)
              AND (:#{#request.tenSanPham} IS NULL OR sp.ten LIKE CONCAT('%', :#{#request.tenSanPham}, '%'))
              AND (:#{#request.tenDanhMuc} IS NULL OR dm.ten LIKE CONCAT('%', :#{#request.tenDanhMuc}, '%'))
              AND (:#{#request.tenChatLieu} IS NULL OR cl.ten LIKE CONCAT('%', :#{#request.tenChatLieu}, '%'))
              AND (:#{#request.tenKieuDang} IS NULL OR kd.ten LIKE CONCAT('%', :#{#request.tenKieuDang}, '%'))
              AND (:#{#request.tenThuongHieu} IS NULL OR th.ten LIKE CONCAT('%', :#{#request.tenThuongHieu}, '%'))
        """
            , nativeQuery = true)
    Page<ClientProductProjectionResponse> getAllProducts(Pageable pageable, ClientProductSearchRequest request);


    @Query(value = """
                        SELECT  
                                spct.id as id,
                                spct.ma_san_pham_chi_tiet as maSPCT,
                                sp.ten as ten,
                                sp.mo_ta as moTa,
                                cl.ten as chatLieu,
                                dm.ten as danhMuc,
                                ca.ten as coAo,
                                ta.ten as tayAo,
                                ht.ten as hoaTiet,
                                tn.ten as tinhNang,
                                spct.so_luong as soLuong,
                                th.ten as thuongHieu,
                                kd.ten as kieuDang,
                                spct.gia AS gia,
                                kc.ten AS size,
                                ms.ten AS color,
                                anh.url AS anh
                        FROM san_pham_chi_tiet spct
                             JOIN san_pham sp ON sp.id = spct.id_san_pham
                             LEFT JOIN danh_muc dm ON dm.id = sp.id_danh_muc
                             LEFT JOIN chat_lieu cl ON cl.id = spct.id_chat_lieu
                             LEFT JOIN co_ao ca ON ca.id = spct.id_co_ao
                             LEFT JOIN tay_ao ta ON ta.id = spct.id_tay_ao
                             LEFT JOIN hoa_tiet ht ON ht.id = spct.id_hoa_tiet
                             LEFT JOIN tinh_nang tn ON tn.id = spct.id_tinh_nang
                             LEFT JOIN thuong_hieu th ON th.id = spct.id_thuong_hieu
                             LEFT JOIN kieu_dang kd ON kd.id = spct.id_kieu_dang
                             LEFT JOIN mau_sac ms ON ms.id = spct.id_mau_sac
                             LEFT JOIN kich_co kc ON kc.id = spct.id_kich_co
                                      LEFT JOIN anh ON anh.id_san_pham_chi_tiet = spct.id
                        WHERE   sp.trang_thai = '0'
                                AND sp.id = :#{#idSanPham} 
                                AND (:#{#request.tenSanPham} IS NULL OR sp.ten LIKE CONCAT('%', :#{#request.tenSanPham}, '%'))
                                AND (:#{#request.tenDanhMuc} IS NULL OR dm.ten LIKE CONCAT('%', :#{#request.tenDanhMuc}, '%'))
                                AND (:#{#request.tenChatLieu} IS NULL OR cl.ten LIKE CONCAT('%', :#{#request.tenChatLieu}, '%'))
                                AND (:#{#request.tenCoAo} IS NULL OR ca.ten LIKE CONCAT('%', :#{#request.tenCoAo}, '%'))
                                AND (:#{#request.tenHoaTiet} IS NULL OR ht.ten LIKE CONCAT('%', :#{#request.tenHoaTiet}, '%'))
                                AND (:#{#request.tenTinhNang} IS NULL OR tn.ten LIKE CONCAT('%', :#{#request.tenTinhNang}, '%'))
                                AND (:#{#request.tenKieuDang} IS NULL OR kd.ten LIKE CONCAT('%', :#{#request.tenKieuDang}, '%'))
                                AND (:#{#request.tenThuongHieu} IS NULL OR th.ten LIKE CONCAT('%', :#{#request.tenThuongHieu}, '%'))
                                AND (:#{#request.tenTayAo} IS NULL OR ta.ten LIKE CONCAT('%', :#{#request.tenTayAo}, '%'))
            """, nativeQuery = true)
    List<ClientProductDetailResponse> getProductDetail(String idSanPham, ClientProductSearchRequest request);

    @Query(value = """
                        SELECT  ROW_NUMBER() OVER(ORDER BY MAX(spct.ngay_tao) DESC) AS catalog,
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
                                ht.id as idHoaTiet,
                                ht.ten as tenHoaTiet,
                                tn.id as idTinhNang,
                                tn.ten as tenTinhNang,
                                th.id as idThuongHieu,
                                th.ten as tenThuongHieu,
                                kd.id as idKieuDang,
                                kd.ten as tenKieuDang,
                                spct.gioi_tinh as gioiTinh,
                                GROUP_CONCAT(DISTINCT spct.id) as maSPCTs,
                                GROUP_CONCAT(DISTINCT best_discounts.gia_sau_giam ORDER BY best_discounts.gia_sau_giam ASC) AS discount,
                                GROUP_CONCAT(DISTINCT spct.gia ORDER BY spct.gia ASC) AS gia,
                                GROUP_CONCAT(DISTINCT
                                    CASE
                                        WHEN best_discounts.gia_sau_giam IS NOT NULL
                                        THEN CONCAT(ROUND((spct.gia - best_discounts.gia_sau_giam) / spct.gia * 100, 1), '%')
                                        ELSE NULL
                                        END
                                        ORDER BY (CASE WHEN best_discounts.gia_sau_giam IS NOT NULL
                                        THEN (spct.gia - best_discounts.gia_sau_giam) / spct.gia
                                        ELSE 0 END) DESC
                                    ) AS phanTramGiam,
                                GROUP_CONCAT(DISTINCT CONCAT(kc.id, ':', kc.ten)) AS kichCos,
                                GROUP_CONCAT(DISTINCT CONCAT(ms.id,':' ,ms.ma_mau_sac, ':', ms.ten)) AS colors,
                                GROUP_CONCAT(DISTINCT anh.url) AS anhs
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
                        LEFT JOIN (
                            SELECT
                               spgg.id_san_pham_chi_tiet,
                               spgg.gia_sau_giam,
                               spgg.id_dot_giam_gia,
                               ROW_NUMBER() OVER (
                                     PARTITION BY spgg.id_san_pham_chi_tiet
                                        ORDER BY (spct_inner.gia - spgg.gia_sau_giam) / spct_inner.gia DESC) 
                                        as rank_num
                            FROM san_pham_giam_gia spgg
                            JOIN dot_giam_gia dgg ON dgg.id = spgg.id_dot_giam_gia
                            JOIN san_pham_chi_tiet spct_inner ON spct_inner.id = spgg.id_san_pham_chi_tiet
                            WHERE dgg.ngay_bat_dau <= UNIX_TIMESTAMP() * 1000
                            AND dgg.ngay_ket_thuc >= UNIX_TIMESTAMP() * 1000
                            AND dgg.trang_thai = 'ACTIVE'
                            AND dgg.deleted = 0
                            ) best_discounts ON best_discounts.id_san_pham_chi_tiet = spct.id AND best_discounts.rank_num = 1
                        LEFT JOIN dot_giam_gia dgg ON dgg.id = best_discounts.id_dot_giam_gia
                        WHERE sp.trang_thai = 0
                        AND spct.so_luong > 0
                        AND spct.trang_thai = 0
                        AND spct.so_luong IS NOT NULL
                        AND (:#{#request.tenSanPham} IS NULL OR sp.ten LIKE CONCAT('%', :#{#request.tenSanPham}, '%'))
                        AND (:#{#request.tenDanhMuc} IS NULL OR dm.ten LIKE CONCAT('%', :#{#request.tenDanhMuc}, '%'))
                        AND (:#{#request.tenChatLieu} IS NULL OR cl.ten LIKE CONCAT('%', :#{#request.tenChatLieu}, '%'))
                        AND (:#{#request.tenKieuDang} IS NULL OR kd.ten LIKE CONCAT('%', :#{#request.tenKieuDang}, '%'))
                        AND (:#{#request.tenThuongHieu} IS NULL OR th.ten LIKE CONCAT('%', :#{#request.tenThuongHieu}, '%'))
                        GROUP BY sp.id, sp.ten, cl.id, cl.ten, dm.id, dm.ten, ca.id, ca.ten, ta.id, ta.ten,spct.gioi_tinh,
                                 ht.id, ht.ten, tn.id, tn.ten, th.id, th.ten, kd.id, kd.ten, sp.ma_san_pham, sp.mo_ta
                        LIMIT 8
            """, nativeQuery = true)
    List<ClientProductProjectionResponse> getTop8Products(ClientProductSearchRequest request);


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
                                spct.gioi_tinh as gioiTinh,
                                GROUP_CONCAT(DISTINCT spct.id) as maSPCTs,
                                GROUP_CONCAT(DISTINCT 
                                        CASE 
                                            WHEN dgg.id IS NOT NULL 
                                                AND dgg.ngay_bat_dau <= UNIX_TIMESTAMP() * 1000 
                                                AND dgg.ngay_ket_thuc >= UNIX_TIMESTAMP() * 1000 
                                                AND dgg.trang_thai = 'ACTIVE'
                                                AND dgg.deleted = 0 
                                            THEN spgg.gia_sau_giam 
                                            ELSE NULL 
                                        END 
                                        ORDER BY spgg.gia_sau_giam ASC) AS discount,
                                GROUP_CONCAT(DISTINCT
                                    CASE
                                        WHEN (SELECT MIN(spgg_inner.gia_sau_giam) \s
                                             FROM san_pham_giam_gia spgg_inner\s
                                             JOIN dot_giam_gia dgg_inner ON dgg_inner.id = spgg_inner.id_dot_giam_gia\s
                                             WHERE spgg_inner.id_san_pham_chi_tiet = spct.id
                                               AND dgg_inner.ngay_bat_dau <= UNIX_TIMESTAMP() * 1000\s
                                               AND dgg_inner.ngay_ket_thuc >= UNIX_TIMESTAMP() * 1000\s
                                               AND dgg_inner.trang_thai = "ACTIVE"
                                               AND dgg_inner.deleted = 0) IS NOT NULL
                                        THEN CONCAT(ROUND((spct.gia - (SELECT MIN(spgg_inner.gia_sau_giam) \s
                                                          FROM san_pham_giam_gia spgg_inner\s
                                                          JOIN dot_giam_gia dgg_inner ON dgg_inner.id = spgg_inner.id_dot_giam_gia\s
                                                          WHERE spgg_inner.id_san_pham_chi_tiet = spct.id
                                                            AND dgg_inner.ngay_bat_dau <= UNIX_TIMESTAMP() * 1000\s
                                                            AND dgg_inner.ngay_ket_thuc >= UNIX_TIMESTAMP() * 1000\s
                                                            AND dgg_inner.trang_thai = "ACTIVE"
                                                            AND dgg_inner.deleted = 0)) / spct.gia * 100, 1), '%')
                                        ELSE NULL
                                    END
                                    ORDER BY
                                        CASE
                                            WHEN (SELECT MIN(spgg_inner.gia_sau_giam) FROM san_pham_giam_gia spgg_inner\s
                                                 JOIN dot_giam_gia dgg_inner ON dgg_inner.id = spgg_inner.id_dot_giam_gia\s
                                                 WHERE spgg_inner.id_san_pham_chi_tiet = spct.id
                                                   AND dgg_inner.ngay_bat_dau <= UNIX_TIMESTAMP() * 1000\s
                                                   AND dgg_inner.ngay_ket_thuc >= UNIX_TIMESTAMP() * 1000\s
                                                   AND dgg_inner.trang_thai = "ACTIVE"
                                                   AND dgg_inner.deleted = 0) IS NOT NULL
                                            THEN (spct.gia - (SELECT MIN(spgg_inner.gia_sau_giam) FROM san_pham_giam_gia spgg_inner\s
                                                             JOIN dot_giam_gia dgg_inner ON dgg_inner.id = spgg_inner.id_dot_giam_gia\s
                                                             WHERE spgg_inner.id_san_pham_chi_tiet = spct.id
                                                               AND dgg_inner.ngay_bat_dau <= UNIX_TIMESTAMP() * 1000\s
                                                               AND dgg_inner.ngay_ket_thuc >= UNIX_TIMESTAMP() * 1000\s
                                                               AND dgg_inner.trang_thai = 'ACTIVE'
                                                               AND dgg_inner.deleted = 0)) / spct.gia
                                            ELSE 0
                                        END DESC
                                    ) AS phanTramGiam,        
                                GROUP_CONCAT( spct.gia ORDER BY spct.gia ASC) AS gia,
                                GROUP_CONCAT(DISTINCT CONCAT(kc.id, ':', kc.ten)) AS kichCos,
                                GROUP_CONCAT(DISTINCT CONCAT(ms.id,':' ,ms.ma_mau_sac, ':', ms.ten)) AS colors,
                                GROUP_CONCAT(DISTINCT CONCAT(anh.url)) AS anhs
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
                                        LEFT JOIN san_pham_giam_gia spgg ON  spgg.id_san_pham_chi_tiet = spct.id
                                        LEFT JOIN dot_giam_gia dgg ON dgg.id = spgg.id_dot_giam_gia            
                        WHERE   sp.trang_thai = 0
                                AND  sp.id = :#{#idSanPham} 
                                AND  dm.id = :#{#request.idDanhMuc}
                                AND  cl.id =   :#{#request.idChatLieu}
                                AND  ca.id =  :#{#request.idCoAo}
                                AND  ht.id =  :#{#request.idHoaTiet}
                                AND  tn.id =  :#{#request.idTinhNang}
                                AND  kd.id =  :#{#request.idKieuDang}
                                AND  th.id =  :#{#request.idThuongHieu}
                                AND  ta.id =  :#{#request.idTayAo}
                                AND  spct.gioi_tinh = :#{#request.gioiTinh}
                                AND spct.so_luong > 0
                                AND spct.trang_thai = 0    
                        GROUP BY sp.id, sp.ten ,cl.id, cl.ten,dm.id, dm.ten,ca.id, ca.ten,ta.id, ta.ten,ht.id, ht.ten,tn.id, tn.ten,th.id, th.ten,kd.id, kd.ten, sp.ma_san_pham,spct.gioi_tinh  
            """, nativeQuery = true)
    ClientProductProjectionResponse getProductById(String idSanPham, ClientProductRequest request);


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
                ht.id as idHoaTiet,
                ht.ten as tenHoaTiet,
                tn.id as idTinhNang,
                tn.ten as tenTinhNang,
                th.id as idThuongHieu,
                th.ten as tenThuongHieu,
                kd.id as idKieuDang,
                kd.ten as tenKieuDang,
                spct.gioi_tinh as gioiTinh,
                GROUP_CONCAT(DISTINCT spct.id) as maSPCTs,
                GROUP_CONCAT(DISTINCT spct.gia ORDER BY spct.gia ASC) AS gia,
                GROUP_CONCAT(DISTINCT\s
                    (SELECT MIN(spgg_inner.gia_sau_giam) \s
                     FROM san_pham_giam_gia spgg_inner\s
                     JOIN dot_giam_gia dgg_inner ON dgg_inner.id = spgg_inner.id_dot_giam_gia\s
                     WHERE spgg_inner.id_san_pham_chi_tiet = spct.id
                       AND dgg_inner.ngay_bat_dau <= UNIX_TIMESTAMP() * 1000\s
                       AND dgg_inner.ngay_ket_thuc >= UNIX_TIMESTAMP() * 1000\s
                       AND dgg_inner.trang_thai = "ACTIVE"
                       AND dgg_inner.deleted = 0)
                     ORDER BY 1 ASC) AS discount,
                GROUP_CONCAT(DISTINCT
                    CASE
                        WHEN (SELECT MIN(spgg_inner.gia_sau_giam) 
                             FROM san_pham_giam_gia spgg_inner\s
                             JOIN dot_giam_gia dgg_inner ON dgg_inner.id = spgg_inner.id_dot_giam_gia\s
                             WHERE spgg_inner.id_san_pham_chi_tiet = spct.id
                               AND dgg_inner.ngay_bat_dau <= UNIX_TIMESTAMP() * 1000\s
                               AND dgg_inner.ngay_ket_thuc >= UNIX_TIMESTAMP() * 1000\s
                               AND dgg_inner.trang_thai = "ACTIVE"
                               AND dgg_inner.deleted = 0) IS NOT NULL
                        THEN CONCAT(ROUND((spct.gia - (SELECT MIN(spgg_inner.gia_sau_giam) \s
                                          FROM san_pham_giam_gia spgg_inner\s
                                          JOIN dot_giam_gia dgg_inner ON dgg_inner.id = spgg_inner.id_dot_giam_gia\s
                                          WHERE spgg_inner.id_san_pham_chi_tiet = spct.id
                                            AND dgg_inner.ngay_bat_dau <= UNIX_TIMESTAMP() * 1000\s
                                            AND dgg_inner.ngay_ket_thuc >= UNIX_TIMESTAMP() * 1000\s
                                            AND dgg_inner.trang_thai = 'ACTIVE'
                                            AND dgg_inner.deleted = 0)) / spct.gia * 100, 1), '%')
                        ELSE NULL
                    END
                    ORDER BY\s
                        CASE\s
                            WHEN (SELECT MIN(spgg_inner.gia_sau_giam) FROM san_pham_giam_gia spgg_inner\s
                                 JOIN dot_giam_gia dgg_inner ON dgg_inner.id = spgg_inner.id_dot_giam_gia\s
                                 WHERE spgg_inner.id_san_pham_chi_tiet = spct.id
                                   AND dgg_inner.ngay_bat_dau <= UNIX_TIMESTAMP() * 1000\s
                                   AND dgg_inner.ngay_ket_thuc >= UNIX_TIMESTAMP() * 1000\s
                                   AND dgg_inner.trang_thai = "ACTIVE"
                                   AND dgg_inner.deleted = 0) IS NOT NULL
                            THEN (spct.gia - (SELECT MIN(spgg_inner.gia_sau_giam) FROM san_pham_giam_gia spgg_inner\s
                                             JOIN dot_giam_gia dgg_inner ON dgg_inner.id = spgg_inner.id_dot_giam_gia\s
                                             WHERE spgg_inner.id_san_pham_chi_tiet = spct.id
                                               AND dgg_inner.ngay_bat_dau <= UNIX_TIMESTAMP() * 1000\s
                                               AND dgg_inner.ngay_ket_thuc >= UNIX_TIMESTAMP() * 1000\s
                                               AND dgg_inner.trang_thai = 'ACTIVE'
                                               AND dgg_inner.deleted = 0)) / spct.gia
                            ELSE 0
                        END DESC
                ) AS phanTramGiam,
                GROUP_CONCAT(DISTINCT CONCAT(kc.id, ':', kc.ten)) AS kichCos,
                GROUP_CONCAT(DISTINCT CONCAT(ms.id,':' ,ms.ma_mau_sac, ':', ms.ten)) AS colors,
                GROUP_CONCAT(DISTINCT anh.url) AS anhs
            FROM san_pham sp
            JOIN san_pham_chi_tiet spct ON sp.id = spct.id_san_pham\s
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
            WHERE sp.trang_thai = 0
                AND spct.trang_thai = 0
                AND spct.so_luong >0
                AND (sp.id = :#{#idSanPham} OR :#{#idSanPham} IS NULL)
                AND (dm.id = :#{#request.idDanhMuc} OR :#{#request.idDanhMuc} IS NULL)
                AND (cl.id = :#{#request.idChatLieu} OR :#{#request.idChatLieu} IS NULL)
                AND (ca.id = :#{#request.idCoAo} OR :#{#request.idCoAo} IS NULL)
                AND (ht.id = :#{#request.idHoaTiet} OR :#{#request.idHoaTiet} IS NULL)
                AND (tn.id = :#{#request.idTinhNang} OR :#{#request.idTinhNang} IS NULL)
                AND (kd.id = :#{#request.idKieuDang} OR :#{#request.idKieuDang} IS NULL)
                AND (th.id = :#{#request.idThuongHieu} OR :#{#request.idThuongHieu} IS NULL)
                AND (ta.id = :#{#request.idTayAo} OR :#{#request.idTayAo} IS NULL)
                AND (kc.id = :#{#request.idKichCo} OR :#{#request.idKichCo} IS NULL)
                AND (ms.id = :#{#request.idMauSac} OR :#{#request.idMauSac} IS NULL)
                AND (spct.gioi_tinh = :#{#request.gioiTinh} OR :#{#request.gioiTinh} IS NULL)
            GROUP BY sp.id, sp.ten, cl.id, cl.ten, dm.id, dm.ten, ca.id, ca.ten, ta.id, ta.ten,
                     ht.id, ht.ten, tn.id, tn.ten, th.id, th.ten, kd.id, kd.ten, sp.ma_san_pham, sp.mo_ta,spct.gioi_tinh
            """, nativeQuery = true)
    ClientProductProjectionResponse getProductDetailById(String idSanPham, ClientProductDetailRequest request);


    @Query(value = """
                        WITH ranked_products AS (
                            SELECT sp.id as product_id,
                                   COALESCE(SUM(hdct.so_luong), 0) AS tongSoLuongBan,
                                   MAX(CASE WHEN dgg.id IS NOT NULL 
                                        AND dgg.ngay_bat_dau <= UNIX_TIMESTAMP() * 1000 
                                        AND dgg.ngay_ket_thuc >= UNIX_TIMESTAMP() * 1000  
                                        AND dgg.trang_thai = 1 THEN 1 ELSE 0 END) as has_discount
                            FROM san_pham sp
                            JOIN san_pham_chi_tiet spct ON sp.id = spct.id_san_pham
                            LEFT JOIN hoa_don_chi_tiet hdct ON hdct.id_san_pham_chi_tiet = spct.id
                            LEFT JOIN hoa_don hd ON hd.id = hdct.id_hoa_don AND hd.trang_thai = "Đã hoàn thành"
                            LEFT JOIN san_pham_giam_gia spgg ON spgg.id_san_pham_chi_tiet = spct.id
                            LEFT JOIN dot_giam_gia dgg ON dgg.id = spgg.id_dot_giam_gia
                            WHERE sp.trang_thai = 0
                                  AND spct.so_luong > 0
                                  AND spct.so_luong IS NOT NULL
                                  AND spct.trang_thai = 0
                            GROUP BY sp.id
                            ORDER BY tongSoLuongBan DESC, has_discount DESC
                            LIMIT 8
                        )
                        SELECT  ROW_NUMBER() OVER(ORDER BY rp.tongSoLuongBan DESC, rp.has_discount DESC) AS catalog,
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
                                spct.gioi_tinh as gioiTinh,
                                GROUP_CONCAT(DISTINCT spct.id) as maSPCTs,
                                GROUP_CONCAT(DISTINCT 
                                    CASE 
                                        WHEN dgg.id IS NOT NULL 
                                            AND dgg.ngay_bat_dau <= UNIX_TIMESTAMP() * 1000 
                                            AND dgg.ngay_ket_thuc >= UNIX_TIMESTAMP() * 1000 
                                            AND dgg.trang_thai = "ACTIVE"
                                            AND dgg.deleted = 0 
                                        THEN spgg.gia_sau_giam 
                                        ELSE NULL 
                                    END 
                                    ORDER BY spgg.gia_sau_giam ASC) AS discount,
                                GROUP_CONCAT(DISTINCT spct.gia ORDER BY spct.gia ASC) AS gia,
                                GROUP_CONCAT(DISTINCT 
                                    CASE 
                                        WHEN dgg.id IS NOT NULL 
                                            AND dgg.ngay_bat_dau <= UNIX_TIMESTAMP() * 1000 
                                            AND dgg.ngay_ket_thuc >= UNIX_TIMESTAMP() * 1000 
                                            AND dgg.trang_thai = "ACTIVE"
                                            AND dgg.deleted = 0 
                                        THEN CONCAT(ROUND((spct.gia - spgg.gia_sau_giam) / spct.gia * 100, 1), '%') 
                                        ELSE NULL 
                                    END 
                                    ORDER BY ((spct.gia - COALESCE(spgg.gia_sau_giam, 0)) / spct.gia) DESC) AS phanTramGiam,
                                GROUP_CONCAT(DISTINCT CONCAT(kc.id, ':', kc.ten)) AS kichCos,
                                GROUP_CONCAT(DISTINCT CONCAT(ms.id,':' ,ms.ma_mau_sac, ':', ms.ten)) AS colors,
                                GROUP_CONCAT(DISTINCT anh.url) AS anhs,
                                rp.tongSoLuongBan AS tongSoLuongBan
                        FROM ranked_products rp
                        JOIN san_pham sp ON sp.id = rp.product_id
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
                        LEFT JOIN san_pham_giam_gia spgg ON spgg.id_san_pham_chi_tiet = spct.id
                        LEFT JOIN dot_giam_gia dgg ON dgg.id = spgg.id_dot_giam_gia 
                        GROUP BY sp.id, sp.ten, cl.id, cl.ten, dm.id, dm.ten, ca.id, ca.ten, ta.id, ta.ten, ht.id, ht.ten, tn.id, tn.ten, th.id, th.ten, kd.id, kd.ten, sp.ma_san_pham, rp.tongSoLuongBan, spct.gioi_tinh
            """, nativeQuery = true)
    List<ClientProductProjectionResponse> getTopProductBestSale();


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
                            ht.id as idHoaTiet,
                            ht.ten as tenHoaTiet,
                            tn.id as idTinhNang,
                            tn.ten as tenTinhNang,
                            th.id as idThuongHieu,
                            th.ten as tenThuongHieu,
                            kd.id as idKieuDang,
                            kd.ten as tenKieuDang,
                            spct.gioi_tinh as gioiTinh,
                            GROUP_CONCAT(DISTINCT spct.id) as maSPCTs,
                            GROUP_CONCAT(DISTINCT best_discounts.gia_sau_giam ORDER BY best_discounts.gia_sau_giam ASC) AS discount,
                            GROUP_CONCAT(DISTINCT spct.gia ORDER BY spct.gia ASC) AS gia,
                            GROUP_CONCAT(DISTINCT CONCAT(ROUND((spct.gia - best_discounts.gia_sau_giam) / spct.gia * 100, 1), '%')
                                        ORDER BY ((spct.gia - best_discounts.gia_sau_giam) / spct.gia) DESC) AS phanTramGiam,
                            GROUP_CONCAT(DISTINCT CONCAT(kc.id, ':', kc.ten)) AS kichCos,
                            GROUP_CONCAT(DISTINCT CONCAT(ms.id,':' ,ms.ma_mau_sac, ':', ms.ten)) AS colors,
                            GROUP_CONCAT(DISTINCT anh.url) AS anhs,
                            GROUP_CONCAT(DISTINCT CONCAT(dgg.id, ':', dgg.ten, ':', best_discounts.gia_sau_giam)) AS thongTinGiamGia
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
                        INNER JOIN (
                            SELECT
                                spgg.id_san_pham_chi_tiet,
                                spgg.gia_sau_giam,
                                spgg.id_dot_giam_gia,
                                ROW_NUMBER() OVER (
                                    PARTITION BY spgg.id_san_pham_chi_tiet
                                    ORDER BY (spct_inner.gia - spgg.gia_sau_giam) / spct_inner.gia DESC
                                ) as rank_num
                            FROM san_pham_giam_gia spgg
                            JOIN dot_giam_gia dgg ON dgg.id = spgg.id_dot_giam_gia
                            JOIN san_pham_chi_tiet spct_inner ON spct_inner.id = spgg.id_san_pham_chi_tiet
                            WHERE dgg.ngay_bat_dau <= UNIX_TIMESTAMP() * 1000
                                AND dgg.ngay_ket_thuc >= UNIX_TIMESTAMP() * 1000
                                AND dgg.trang_thai = "ACTIVE"
                                AND dgg.deleted = 0
                        ) best_discounts ON best_discounts.id_san_pham_chi_tiet = spct.id AND best_discounts.rank_num = 1
                        JOIN dot_giam_gia dgg ON dgg.id = best_discounts.id_dot_giam_gia
                        WHERE sp.trang_thai = 0
                            AND spct.so_luong > 0
                            AND spct.trang_thai = 0          
                            AND spct.so_luong IS NOT NULL
                        GROUP BY sp.id, sp.ten, cl.id, cl.ten, dm.id, dm.ten, ca.id, ca.ten, ta.id, ta.ten,spct.gioi_tinh,
                                 ht.id, ht.ten, tn.id, tn.ten, th.id, th.ten, kd.id, kd.ten, sp.ma_san_pham, sp.mo_ta
            """, nativeQuery = true)
    Page<ClientProductProjectionResponse> getSaleProduct(ClientProductSearchRequest request, Pageable pageable);


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
                ht.id as idHoaTiet,
                ht.ten as tenHoaTiet,
                tn.id as idTinhNang,
                tn.ten as tenTinhNang,
                th.id as idThuongHieu,
                th.ten as tenThuongHieu,
                kd.id as idKieuDang,
                kd.ten as tenKieuDang,
                spct.gioi_tinh as gioiTinh,
                GROUP_CONCAT(DISTINCT spct.id) as maSPCTs,
                GROUP_CONCAT(DISTINCT spct.gia ORDER BY spct.gia ASC) AS gia,
                GROUP_CONCAT(DISTINCT\s
                    (SELECT MIN(spgg_inner.gia_sau_giam) \s
                     FROM san_pham_giam_gia spgg_inner\s
                     JOIN dot_giam_gia dgg_inner ON dgg_inner.id = spgg_inner.id_dot_giam_gia\s
                     WHERE spgg_inner.id_san_pham_chi_tiet = spct.id
                       AND dgg_inner.ngay_bat_dau <= UNIX_TIMESTAMP() * 1000\s
                       AND dgg_inner.ngay_ket_thuc >= UNIX_TIMESTAMP() * 1000\s
                       AND dgg_inner.trang_thai = "ACTIVE"
                       AND dgg_inner.deleted = 0)
                     ORDER BY 1 ASC) AS discount,
                GROUP_CONCAT(DISTINCT
                    CASE
                        WHEN (SELECT MIN(spgg_inner.gia_sau_giam) 
                             FROM san_pham_giam_gia spgg_inner\s
                             JOIN dot_giam_gia dgg_inner ON dgg_inner.id = spgg_inner.id_dot_giam_gia\s
                             WHERE spgg_inner.id_san_pham_chi_tiet = spct.id
                               AND dgg_inner.ngay_bat_dau <= UNIX_TIMESTAMP() * 1000\s
                               AND dgg_inner.ngay_ket_thuc >= UNIX_TIMESTAMP() * 1000\s
                               AND dgg_inner.trang_thai = "ACTIVE"
                               AND dgg_inner.deleted = 0) IS NOT NULL
                        THEN CONCAT(ROUND((spct.gia - (SELECT MIN(spgg_inner.gia_sau_giam) \s
                                          FROM san_pham_giam_gia spgg_inner\s
                                          JOIN dot_giam_gia dgg_inner ON dgg_inner.id = spgg_inner.id_dot_giam_gia\s
                                          WHERE spgg_inner.id_san_pham_chi_tiet = spct.id
                                            AND dgg_inner.ngay_bat_dau <= UNIX_TIMESTAMP() * 1000\s
                                            AND dgg_inner.ngay_ket_thuc >= UNIX_TIMESTAMP() * 1000\s
                                            AND dgg_inner.trang_thai = "ACTIVE"
                                            AND dgg_inner.deleted = 0)) / spct.gia * 100, 1), '%')
                        ELSE NULL
                    END
                    ORDER BY\s
                        CASE\s
                            WHEN (SELECT MIN(spgg_inner.gia_sau_giam) FROM san_pham_giam_gia spgg_inner\s
                                 JOIN dot_giam_gia dgg_inner ON dgg_inner.id = spgg_inner.id_dot_giam_gia\s
                                 WHERE spgg_inner.id_san_pham_chi_tiet = spct.id
                                   AND dgg_inner.ngay_bat_dau <= UNIX_TIMESTAMP() * 1000\s
                                   AND dgg_inner.ngay_ket_thuc >= UNIX_TIMESTAMP() * 1000\s
                                   AND dgg_inner.trang_thai = "ACTIVE"
                                   AND dgg_inner.deleted = 0) IS NOT NULL
                            THEN (spct.gia - (SELECT MIN(spgg_inner.gia_sau_giam) FROM san_pham_giam_gia spgg_inner\s
                                             JOIN dot_giam_gia dgg_inner ON dgg_inner.id = spgg_inner.id_dot_giam_gia\s
                                             WHERE spgg_inner.id_san_pham_chi_tiet = spct.id
                                               AND dgg_inner.ngay_bat_dau <= UNIX_TIMESTAMP() * 1000\s
                                               AND dgg_inner.ngay_ket_thuc >= UNIX_TIMESTAMP() * 1000\s
                                               AND dgg_inner.trang_thai = "ACTIVE"
                                               AND dgg_inner.deleted = 0)) / spct.gia
                            ELSE 0
                        END DESC
                ) AS phanTramGiam,
                GROUP_CONCAT(DISTINCT CONCAT(kc.id, ':', kc.ten)) AS kichCos,
                GROUP_CONCAT(DISTINCT CONCAT(ms.id,':' ,ms.ma_mau_sac, ':', ms.ten)) AS colors,
                GROUP_CONCAT(DISTINCT anh.url) AS anhs
            FROM san_pham sp
            JOIN san_pham_chi_tiet spct ON sp.id = spct.id_san_pham\s
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
            WHERE sp.trang_thai = 0
                AND spct.trang_thai = 0
                AND spct.so_luong >0
                AND (sp.id = :#{#idSanPham} OR :#{#idSanPham} IS NULL)
                AND (dm.id = :#{#request.idDanhMuc} OR :#{#request.idDanhMuc} IS NULL)
                AND (cl.id = :#{#request.idChatLieu} OR :#{#request.idChatLieu} IS NULL)
                AND (ca.id = :#{#request.idCoAo} OR :#{#request.idCoAo} IS NULL)
                AND (ht.id = :#{#request.idHoaTiet} OR :#{#request.idHoaTiet} IS NULL)
                AND (tn.id = :#{#request.idTinhNang} OR :#{#request.idTinhNang} IS NULL)
                AND (kd.id = :#{#request.idKieuDang} OR :#{#request.idKieuDang} IS NULL)
                AND (th.id = :#{#request.idThuongHieu} OR :#{#request.idThuongHieu} IS NULL)
                AND (ta.id = :#{#request.idTayAo} OR :#{#request.idTayAo} IS NULL)
                AND (kc.id = :#{#request.idKichCo} OR :#{#request.idKichCo} IS NULL)
                AND (spct.gioi_tinh = :#{#request.gioiTinh} OR :#{#request.gioiTinh} IS NULL)
            GROUP BY sp.id, sp.ten, cl.id, cl.ten, dm.id, dm.ten, ca.id, ca.ten, ta.id, ta.ten,
                     ht.id, ht.ten, tn.id, tn.ten, th.id, th.ten, kd.id, kd.ten, sp.ma_san_pham, sp.mo_ta,spct.gioi_tinh
            """, nativeQuery = true)
    ClientProductProjectionResponse getProductDetailByIdWithSize(String idSanPham, ClientProductDetailRequest request);

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
                ht.id as idHoaTiet,
                ht.ten as tenHoaTiet,
                tn.id as idTinhNang,
                tn.ten as tenTinhNang,
                th.id as idThuongHieu,
                th.ten as tenThuongHieu,
                kd.id as idKieuDang,
                kd.ten as tenKieuDang,
                spct.gioi_tinh as gioiTinh,
                GROUP_CONCAT(DISTINCT spct.id) as maSPCTs,
                GROUP_CONCAT(DISTINCT spct.gia ORDER BY spct.gia ASC) AS gia,
                GROUP_CONCAT(DISTINCT\s
                    (SELECT MIN(spgg_inner.gia_sau_giam) \s
                     FROM san_pham_giam_gia spgg_inner\s
                     JOIN dot_giam_gia dgg_inner ON dgg_inner.id = spgg_inner.id_dot_giam_gia\s
                     WHERE spgg_inner.id_san_pham_chi_tiet = spct.id
                       AND dgg_inner.ngay_bat_dau <= UNIX_TIMESTAMP() * 1000\s
                       AND dgg_inner.ngay_ket_thuc >= UNIX_TIMESTAMP() * 1000\s
                       AND dgg_inner.trang_thai = "ACTIVE"
                       AND dgg_inner.deleted = 0)
                     ORDER BY 1 ASC) AS discount,
                GROUP_CONCAT(DISTINCT
                    CASE
                        WHEN (SELECT MIN(spgg_inner.gia_sau_giam) 
                             FROM san_pham_giam_gia spgg_inner\s
                             JOIN dot_giam_gia dgg_inner ON dgg_inner.id = spgg_inner.id_dot_giam_gia\s
                             WHERE spgg_inner.id_san_pham_chi_tiet = spct.id
                               AND dgg_inner.ngay_bat_dau <= UNIX_TIMESTAMP() * 1000\s
                               AND dgg_inner.ngay_ket_thuc >= UNIX_TIMESTAMP() * 1000\s
                               AND dgg_inner.trang_thai = "ACTIVE"
                               AND dgg_inner.deleted = 0) IS NOT NULL
                        THEN CONCAT(ROUND((spct.gia - (SELECT MIN(spgg_inner.gia_sau_giam) \s
                                          FROM san_pham_giam_gia spgg_inner\s
                                          JOIN dot_giam_gia dgg_inner ON dgg_inner.id = spgg_inner.id_dot_giam_gia\s
                                          WHERE spgg_inner.id_san_pham_chi_tiet = spct.id
                                            AND dgg_inner.ngay_bat_dau <= UNIX_TIMESTAMP() * 1000\s
                                            AND dgg_inner.ngay_ket_thuc >= UNIX_TIMESTAMP() * 1000\s
                                            AND dgg_inner.trang_thai = "ACTIVE"
                                            AND dgg_inner.deleted = 0)) / spct.gia * 100, 1), '%')
                        ELSE NULL
                    END
                    ORDER BY\s
                        CASE\s
                            WHEN (SELECT MIN(spgg_inner.gia_sau_giam) FROM san_pham_giam_gia spgg_inner\s
                                 JOIN dot_giam_gia dgg_inner ON dgg_inner.id = spgg_inner.id_dot_giam_gia\s
                                 WHERE spgg_inner.id_san_pham_chi_tiet = spct.id
                                   AND dgg_inner.ngay_bat_dau <= UNIX_TIMESTAMP() * 1000\s
                                   AND dgg_inner.ngay_ket_thuc >= UNIX_TIMESTAMP() * 1000\s
                                   AND dgg_inner.trang_thai = "ACTIVE"
                                   AND dgg_inner.deleted = 0) IS NOT NULL
                            THEN (spct.gia - (SELECT MIN(spgg_inner.gia_sau_giam) FROM san_pham_giam_gia spgg_inner\s
                                             JOIN dot_giam_gia dgg_inner ON dgg_inner.id = spgg_inner.id_dot_giam_gia\s
                                             WHERE spgg_inner.id_san_pham_chi_tiet = spct.id
                                               AND dgg_inner.ngay_bat_dau <= UNIX_TIMESTAMP() * 1000\s
                                               AND dgg_inner.ngay_ket_thuc >= UNIX_TIMESTAMP() * 1000\s
                                               AND dgg_inner.trang_thai = "ACTIVE"
                                               AND dgg_inner.deleted = 0)) / spct.gia
                            ELSE 0
                        END DESC
                ) AS phanTramGiam,
                GROUP_CONCAT(DISTINCT CONCAT(kc.id, ':', kc.ten)) AS kichCos,
                GROUP_CONCAT(DISTINCT CONCAT(ms.id,':' ,ms.ma_mau_sac, ':', ms.ten)) AS colors,
                GROUP_CONCAT(DISTINCT anh.url) AS anhs
            FROM san_pham sp
            JOIN san_pham_chi_tiet spct ON sp.id = spct.id_san_pham\s
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
            WHERE sp.trang_thai = 0
                AND spct.trang_thai = 0
                AND spct.so_luong >0
                AND (sp.id = :#{#idSanPham} OR :#{#idSanPham} IS NULL)
                AND (dm.id = :#{#request.idDanhMuc} OR :#{#request.idDanhMuc} IS NULL)
                AND (cl.id = :#{#request.idChatLieu} OR :#{#request.idChatLieu} IS NULL)
                AND (ca.id = :#{#request.idCoAo} OR :#{#request.idCoAo} IS NULL)
                AND (ht.id = :#{#request.idHoaTiet} OR :#{#request.idHoaTiet} IS NULL)
                AND (tn.id = :#{#request.idTinhNang} OR :#{#request.idTinhNang} IS NULL)
                AND (kd.id = :#{#request.idKieuDang} OR :#{#request.idKieuDang} IS NULL)
                AND (th.id = :#{#request.idThuongHieu} OR :#{#request.idThuongHieu} IS NULL)
                AND (ta.id = :#{#request.idTayAo} OR :#{#request.idTayAo} IS NULL)
                AND (ms.id = :#{#request.idMauSac} OR :#{#request.idMauSac} IS NULL)
                AND (spct.gioi_tinh = :#{#request.gioiTinh} OR :#{#request.gioiTinh} IS NULL)
            GROUP BY sp.id, sp.ten, cl.id, cl.ten, dm.id, dm.ten, ca.id, ca.ten, ta.id, ta.ten,
                     ht.id, ht.ten, tn.id, tn.ten, th.id, th.ten, kd.id, kd.ten, sp.ma_san_pham, sp.mo_ta,spct.gioi_tinh
            """, nativeQuery = true)
    ClientProductProjectionResponse getProductDetailByIdWithColor(String idSanPham, ClientProductDetailRequest request);

}
