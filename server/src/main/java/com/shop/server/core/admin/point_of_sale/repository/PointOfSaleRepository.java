package com.shop.server.core.admin.point_of_sale.repository;

import com.shop.server.core.admin.point_of_sale.model.request.AdPOSFindProductRequest;
import com.shop.server.core.admin.point_of_sale.model.response.AdPOSFindProductResponse;
import com.shop.server.core.admin.point_of_sale.model.response.AdPOSOrderDetailResponse;
import com.shop.server.core.admin.point_of_sale.model.response.PriceRank;
import com.shop.server.repositories.SanPhamChiTietRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PointOfSaleRepository extends SanPhamChiTietRepository {

    @Query(value = """
                select ROW_NUMBER() OVER(ORDER BY spct.id DESC) AS catalog,
                    spct.id,
                    spct.ma_san_pham_chi_tiet,
                    spct.ten,
                    spct.gia,
                    tinh_gia_hien_tai(spct.id) as gia_hien_tai,
                    CONCAT(sp.ten, ' - ', dm.ten) as ten_san_pham,
                    th.ten as ten_thuong_hieu,
                    spct.gioi_tinh,
                    spct.so_luong,
                    kc.ten as kich_co,
                    ms.ma_mau_sac,
                    ms.ten as ten_mau_sac,
                    CONCAT(ca.ten, ', ', ta.ten, ', ', ht.ten, ', ', cl.ten, ', ', kd.ten, ', ', tn.ten) as phong_cach,
                    coalesce(anh.url, 'default-product-detail-image-url.jpg') as link_anh
                from san_pham_chi_tiet spct
                join san_pham sp on spct.id_san_pham = sp.id
                join danh_muc dm on dm.id = sp.id_danh_muc
                join thuong_hieu th on spct.id_thuong_hieu = th.id
                join kich_co kc on spct.id_kich_co = kc.id
                join mau_sac ms on spct.id_mau_sac = ms.id
                
                left join co_ao ca on spct.id_co_ao = ca.id
                left join tay_ao ta on spct.id_tay_ao = ta.id
                left join hoa_tiet ht on spct.id_hoa_tiet = ht.id
                left join chat_lieu cl on spct.id_chat_lieu = cl.id
                left join kieu_dang kd on spct.id_kieu_dang = kd.id
                left join tinh_nang tn on spct.id_tinh_nang = tn.id
                left join anh on spct.id = anh.id_san_pham_chi_tiet and (anh.is_top = true)
                where spct.deleted = false and spct.trang_thai = 0 and spct.so_luong > 0
                and (:#{#req.bienGiaLon} is null or (tinh_gia_hien_tai(spct.id) between :#{#req.bienGiaBe} and :#{#req.bienGiaLon}))
                and (:#{#req.gioiTinh} is null or spct.gioi_tinh = :#{#req.gioiTinh})
                and (:#{#req.keyword} is null
                    or ((sp.ten LIKE CONCAT('%', :#{#req.keyword}, '%'))
                    or (dm.ten LIKE CONCAT('%', :#{#req.keyword}, '%'))
                    or (spct.ma_san_pham_chi_tiet LIKE CONCAT('%', :#{#req.keyword}, '%'))
                    or (spct.ten LIKE CONCAT('%', :#{#req.keyword}, '%'))
                    or (ms.ten LIKE CONCAT('%', :#{#req.keyword}, '%'))))
                and (:#{#req.idThuongHieu} is null or spct.id_thuong_hieu = :#{#req.idThuongHieu})
                and (:#{#req.idKichCo} is null or spct.id_kich_co = :#{#req.idKichCo})
                and (:#{#req.idCoAo} is null or spct.id_co_ao = :#{#req.idCoAo})
                and (:#{#req.idTayAo} is null or spct.id_tay_ao = :#{#req.idTayAo})
                and (:#{#req.idHoaTiet} is null or spct.id_hoa_tiet = :#{#req.idHoaTiet})
                and (:#{#req.idChatLieu} is null or spct.id_chat_lieu = :#{#req.idChatLieu})
                and (:#{#req.idKieuDang} is null or spct.id_kieu_dang = :#{#req.idKieuDang})
                and (:#{#req.idTinhNang} is null or spct.id_tinh_nang = :#{#req.idTinhNang})
            """, countQuery = """
                select COUNT(spct.id)
                from san_pham_chi_tiet spct
                join san_pham sp on spct.id_san_pham = sp.id
                join danh_muc dm on dm.id = sp.id_danh_muc
                join thuong_hieu th on spct.id_thuong_hieu = th.id
                join kich_co kc on spct.id_kich_co = kc.id
                join mau_sac ms on spct.id_mau_sac = ms.id
                left join co_ao ca on spct.id_co_ao = ca.id
                left join tay_ao ta on spct.id_tay_ao = ta.id
                left join hoa_tiet ht on spct.id_hoa_tiet = ht.id
                left join chat_lieu cl on spct.id_chat_lieu = cl.id
                left join kieu_dang kd on spct.id_kieu_dang = kd.id
                left join tinh_nang tn on spct.id_tinh_nang = tn.id
                left join anh on spct.id = anh.id_san_pham_chi_tiet and (anh.is_top = true)
                where spct.deleted = false and spct.trang_thai = 0 and spct.so_luong > 0
                and (:#{#req.bienGiaLon} is null or (tinh_gia_hien_tai(spct.id) between :#{#req.bienGiaBe} and :#{#req.bienGiaLon}))
                and (:#{#req.gioiTinh} is null or spct.gioi_tinh = :#{#req.gioiTinh})
                and (:#{#req.keyword} is null
                    or ((sp.ten LIKE CONCAT('%', :#{#req.keyword}, '%'))
                    or (dm.ten LIKE CONCAT('%', :#{#req.keyword}, '%'))
                    or (spct.ma_san_pham_chi_tiet LIKE CONCAT('%', :#{#req.keyword}, '%'))
                    or (spct.ten LIKE CONCAT('%', :#{#req.keyword}, '%'))
                    or (ms.ten LIKE CONCAT('%', :#{#req.keyword}, '%'))))
                and (:#{#req.idThuongHieu} is null or spct.id_thuong_hieu = :#{#req.idThuongHieu})
                and (:#{#req.idKichCo} is null or spct.id_kich_co = :#{#req.idKichCo})
                and (:#{#req.idCoAo} is null or spct.id_co_ao = :#{#req.idCoAo})
                and (:#{#req.idTayAo} is null or spct.id_tay_ao = :#{#req.idTayAo})
                and (:#{#req.idHoaTiet} is null or spct.id_hoa_tiet = :#{#req.idHoaTiet})
                and (:#{#req.idChatLieu} is null or spct.id_chat_lieu = :#{#req.idChatLieu})
                and (:#{#req.idKieuDang} is null or spct.id_kieu_dang = :#{#req.idKieuDang})
                and (:#{#req.idTinhNang} is null or spct.id_tinh_nang = :#{#req.idTinhNang})
            """, nativeQuery = true)
    Page<AdPOSFindProductResponse> getProductDetails(AdPOSFindProductRequest req, Pageable pageable);

    @Query(value = """
    SELECT MIN(price) as min_price, MAX(price) as max_price
    FROM (
        SELECT DISTINCT tinh_gia_hien_tai(spct.id) AS price
        FROM san_pham_chi_tiet spct
        where tinh_gia_hien_tai(spct.id) IS NOT NULL AND tinh_gia_hien_tai(spct.id) != 0
    ) subquery
    """, nativeQuery = true)
    Optional<PriceRank> getPriceRank();


    @Query(value = """
                select ROW_NUMBER() OVER(ORDER BY hdct.ngay_tao DESC) AS catalog,
                    hdct.id,
                    spct.ma_san_pham_chi_tiet,
                    spct.ten,
                    spct.gia,
                    hdct.gia as gia_hien_tai,
                    CONCAT(sp.ten, ' - ', dm.ten) as ten_san_pham,
                    th.ten as ten_thuong_hieu,
                    spct.gioi_tinh,
                    hdct.so_luong,
                    kc.ten as kich_co,
                    ms.ma_mau_sac,
                    ms.ten as ten_mau_sac,
                    CONCAT(ca.ten, ', ', ta.ten, ', ', ht.ten, ', ', cl.ten, ', ', kd.ten, ', ', tn.ten) as phong_cach,
                    coalesce(anh.url, 'default-product-detail-image-url.jpg') as link_anh
                from hoa_don_chi_tiet hdct
                join san_pham_chi_tiet spct on hdct.id_san_pham_chi_tiet = spct.id and hdct.trang_thai = 'PENDING'
                join san_pham sp on spct.id_san_pham = sp.id
                join danh_muc dm on dm.id = sp.id_danh_muc
                join thuong_hieu th on spct.id_thuong_hieu = th.id
                join kich_co kc on spct.id_kich_co = kc.id
                join mau_sac ms on spct.id_mau_sac = ms.id
                left join co_ao ca on spct.id_co_ao = ca.id
                left join tay_ao ta on spct.id_tay_ao = ta.id
                left join hoa_tiet ht on spct.id_hoa_tiet = ht.id
                left join chat_lieu cl on spct.id_chat_lieu = cl.id
                left join kieu_dang kd on spct.id_kieu_dang = kd.id
                left join tinh_nang tn on spct.id_tinh_nang = tn.id
                left join anh on spct.id = anh.id_san_pham_chi_tiet and (anh.is_top = true)
                where hdct.id_hoa_don = ?1 and hdct.trang_thai = 'PENDING' and hdct.deleted = false
                
            """, nativeQuery = true)
    List<AdPOSOrderDetailResponse> getProductsInOrder(String idOrder);
}
