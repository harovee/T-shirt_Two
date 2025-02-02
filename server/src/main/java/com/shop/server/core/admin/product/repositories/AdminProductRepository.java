package com.shop.server.core.admin.product.repositories;

import com.shop.server.core.admin.product.models.requests.AdGetSanPhamChiTietRequest;
import com.shop.server.core.admin.product.models.requests.AdminFindProductRequest;
import com.shop.server.core.admin.product.models.responses.AdProductListResponse;
import com.shop.server.core.admin.product.models.responses.AdminProductResponse;
import com.shop.server.core.admin.san_pham_chi_tiet.model.response.AdSanPhamChiTietResponse;
import com.shop.server.repositories.SanPhamRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminProductRepository extends SanPhamRepository {

    /**
     * Lấy dữ liệu product dựa trên các trường có trong request
     * @param pageable
     * @param req
     * @return
     */
    @Query(value = """
        SELECT 
            ROW_NUMBER() OVER(ORDER BY sp.ngay_tao DESC) AS catalog,
            sp.id as id,
            sp.ma_san_pham as maSanPham,
            sp.ten AS ten,
            dm.ten AS tenDanhMuc,
            sp.ngay_tao AS ngayTao,
            COALESCE((SELECT SUM(so_luong) FROM san_pham_chi_tiet WHERE id_san_pham = sp.id), 0) AS soLuong,
            sp.trang_thai AS trangThai
        FROM san_pham sp
        LEFT JOIN san_pham_chi_tiet spct ON sp.id = spct.id_san_pham
        JOIN danh_muc dm ON dm.id = sp.id_danh_muc
        WHERE
            (:#{#req.keyword} IS NULL OR sp.ten LIKE CONCAT('%', :#{#req.keyword}, '%'))
            AND (:#{#req.status} IS NULL OR sp.trang_thai = :#{#req.status})
            AND sp.deleted = 0
        GROUP BY sp.id
    """, countQuery = """
        SELECT COUNT(sp.id)
        FROM san_pham sp
        WHERE
            (:#{#req.keyword} IS NULL OR sp.ten LIKE CONCAT('%', :#{#req.keyword}, '%'))
              AND (:#{#req.status} IS NULL OR sp.trang_thai = :#{#req.status})
            AND sp.deleted = 0
    """, nativeQuery = true)
    Page<AdminProductResponse> getProductsByRequest(Pageable pageable, AdminFindProductRequest req);

    @Query(value = """
                SELECT
        			ROW_NUMBER() OVER(ORDER BY spct.ngay_tao DESC) AS catalog,
                    spct.id as id,
                    spct.ma_san_pham_chi_tiet as maSanPhamChitiet,
                    spct.ten AS ten,
                    cl.ten AS chatLieu,
                    ca.ten AS coAo,
                    ht.ten AS hoaTiet,
                    kc.ten AS kichCo,
                    kd.ten AS kieuDang,
                    ms.ten AS mauSac,
                    ta.ten AS tayAo,
                    th.ten AS thuongHieu,
                    tn.ten AS tinhNang,
                    dm.ten AS danhMuc,
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
                    JOIN danh_muc dm ON sp.id_danh_muc = dm.id
        		WHERE spct.id_san_pham = :id
                    AND spct.deleted = 0
    """, countQuery = """
        SELECT COUNT(spct.id)
        FROM san_pham_chi_tiet spct
                JOIN san_pham sp ON spct.id_san_pham = sp.id
                JOIN danh_muc dm ON sp.id_danh_muc = dm.id
            WHERE spct.id_san_pham = :id
                
                AND sp.deleted = 0
    """, nativeQuery = true)
    Page<AdSanPhamChiTietResponse> getSanPhamChiTietByIdSanPham(Pageable pageable, @Param("id") String id);



    @Query(value = """
    SELECT sp.id AS id,
    sp.ten AS ten
    FROM SanPham sp
    ORDER BY sp.createdDate DESC
""")
    List<AdProductListResponse> getListProduct ();

    Boolean existsSanPhamByMaSanPham(String maSanPham);

    Boolean existsSanPhamByTen(String ten);

    @Query("""
        SELECT sp
        FROM SanPham sp
        WHERE sp.ten = :ten AND sp.id != :id
""")
    AdminProductResponse existsSanPhamByNameAndIdNotEx(@Param("ten") String name, @Param("id") String id);
}
