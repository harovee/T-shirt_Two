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
                            LEFT JOIN san_pham_giam_gia spgg ON  spgg.id_san_pham_chi_tiet = spct.id
                            LEFT JOIN dot_giam_gia dgg ON dgg.id = spgg.id_dot_giam_gia
            WHERE   sp.trang_thai = 0
                    AND spct.so_luong >0
                    AND (:#{#request.tenSanPham} IS NULL OR sp.ten LIKE CONCAT('%', :#{#request.tenSanPham}, '%'))
                    AND (:#{#request.tenDanhMuc} IS NULL OR dm.ten LIKE CONCAT('%', :#{#request.tenDanhMuc}, '%'))
                    AND (:#{#request.tenChatLieu} IS NULL OR cl.ten LIKE CONCAT('%', :#{#request.tenChatLieu}, '%'))
                    AND (:#{#request.tenCoAo} IS NULL OR ca.ten LIKE CONCAT('%', :#{#request.tenCoAo}, '%'))
                    AND (:#{#request.tenHoaTiet} IS NULL OR ht.ten LIKE CONCAT('%', :#{#request.tenHoaTiet}, '%'))
                    AND (:#{#request.tenTinhNang} IS NULL OR tn.ten LIKE CONCAT('%', :#{#request.tenTinhNang}, '%'))
                    AND (:#{#request.tenKieuDang} IS NULL OR kd.ten LIKE CONCAT('%', :#{#request.tenKieuDang}, '%'))
                    AND (:#{#request.tenThuongHieu} IS NULL OR th.ten LIKE CONCAT('%', :#{#request.tenThuongHieu}, '%'))
                    AND (:#{#request.tenTayAo} IS NULL OR ta.ten LIKE CONCAT('%', :#{#request.tenTayAo}, '%'))
            GROUP BY sp.id, sp.ten ,cl.id, cl.ten,dm.id, dm.ten,ca.id, ca.ten,ta.id, ta.ten,ht.id, ht.ten,tn.id, tn.ten,th.id, th.ten,kd.id, kd.ten, sp.ma_san_pham
""",nativeQuery = true)
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
""",nativeQuery = true)
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
                            LEFT JOIN san_pham_giam_gia spgg ON  spgg.id_san_pham_chi_tiet = spct.id
                            LEFT JOIN dot_giam_gia dgg ON dgg.id = spgg.id_dot_giam_gia            
            WHERE   sp.trang_thai = 0
                    AND spct.so_luong >0
                    AND (:#{#request.tenSanPham} IS NULL OR sp.ten LIKE CONCAT('%', :#{#request.tenSanPham}, '%'))
                    AND (:#{#request.tenDanhMuc} IS NULL OR dm.ten LIKE CONCAT('%', :#{#request.tenDanhMuc}, '%'))
                    AND (:#{#request.tenChatLieu} IS NULL OR cl.ten LIKE CONCAT('%', :#{#request.tenChatLieu}, '%'))
                    AND (:#{#request.tenCoAo} IS NULL OR ca.ten LIKE CONCAT('%', :#{#request.tenCoAo}, '%'))
                    AND (:#{#request.tenHoaTiet} IS NULL OR ht.ten LIKE CONCAT('%', :#{#request.tenHoaTiet}, '%'))
                    AND (:#{#request.tenTinhNang} IS NULL OR tn.ten LIKE CONCAT('%', :#{#request.tenTinhNang}, '%'))
                    AND (:#{#request.tenKieuDang} IS NULL OR kd.ten LIKE CONCAT('%', :#{#request.tenKieuDang}, '%'))
                    AND (:#{#request.tenThuongHieu} IS NULL OR th.ten LIKE CONCAT('%', :#{#request.tenThuongHieu}, '%'))
                    AND (:#{#request.tenTayAo} IS NULL OR ta.ten LIKE CONCAT('%', :#{#request.tenTayAo}, '%'))
            GROUP BY sp.id, sp.ten ,cl.id, cl.ten,dm.id, dm.ten,ca.id, ca.ten,ta.id, ta.ten,ht.id, ht.ten,tn.id, tn.ten,th.id, th.ten,kd.id, kd.ten, sp.ma_san_pham
            LIMIT 8
""",nativeQuery = true)
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
                    GROUP_CONCAT(DISTINCT spct.ma_san_pham_chi_tiet) as maSPCTs,
                    GROUP_CONCAT(DISTINCT spgg.gia_sau_giam ORDER BY spgg.gia_sau_giam ASC) AS discount,
                    GROUP_CONCAT( spct.gia ORDER BY spct.gia ASC) AS gia,
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
            GROUP BY sp.id, sp.ten ,cl.id, cl.ten,dm.id, dm.ten,ca.id, ca.ten,ta.id, ta.ten,ht.id, ht.ten,tn.id, tn.ten,th.id, th.ten,kd.id, kd.ten, sp.ma_san_pham
""",nativeQuery = true)
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
                    GROUP_CONCAT( spct.gia ORDER BY spct.gia ASC) AS gia,
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
                    AND  kc.id = :#{#request.idKichCo} 
                    AND ms.id = :#{#request.idMauSac}
            GROUP BY sp.id, sp.ten ,cl.id, cl.ten,dm.id, dm.ten,ca.id, ca.ten,ta.id, ta.ten,ht.id, ht.ten,tn.id, tn.ten,th.id, th.ten,kd.id, kd.ten, sp.ma_san_pham
""",nativeQuery = true)
    ClientProductProjectionResponse getProductDetailById(String idSanPham, ClientProductDetailRequest request);
}
