package com.shop.server.core.admin.sale.repositories;

import com.shop.server.core.admin.sale.models.requests.AdminFindProductDetailSaleModuleRequest;
import com.shop.server.core.admin.sale.models.requests.AdminFindProductSaleModuleRequest;
import com.shop.server.core.admin.sale.models.responses.AdminAttributeSaleModuleResponse;
import com.shop.server.core.admin.sale.models.responses.AdminProductDetailSaleModuleResponse;
import com.shop.server.core.admin.sale.models.responses.AdminProductSaleModuleResponse;
import com.shop.server.repositories.SanPhamRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdminProductSaleModuleRepository extends SanPhamRepository {

    @Query(value = """
                select dm.id, dm.ten from danh_muc dm where dm.deleted = 0
            """, nativeQuery = true)
    List<AdminAttributeSaleModuleResponse> getCategories();

    @Query(value = """
                select th.id, th.ten from thuong_hieu th where th.deleted = 0
            """, nativeQuery = true)
    List<AdminAttributeSaleModuleResponse> getBrands();

    @Query(value = """
                select tb.id, tb.ten from kich_co tb where tb.deleted = 0
            """, nativeQuery = true)
    List<AdminAttributeSaleModuleResponse> getSizes();

    @Query(value = """
                select tb.id, tb.ten from co_ao tb where tb.deleted = 0
            """, nativeQuery = true)
    List<AdminAttributeSaleModuleResponse> getCollars();

    @Query(value = """
                select tb.id, tb.ten from tay_ao tb where tb.deleted = 0
            """, nativeQuery = true)
    List<AdminAttributeSaleModuleResponse> getSleeves();

    @Query(value = """
                select tb.id, tb.ten from hoa_tiet tb where tb.deleted = 0
            """, nativeQuery = true)
    List<AdminAttributeSaleModuleResponse> getVignettes();

    @Query(value = """
                select tb.id, tb.ten from chat_lieu tb where tb.deleted = 0
            """, nativeQuery = true)
    List<AdminAttributeSaleModuleResponse> getMaterials();

    @Query(value = """
                select tb.id, tb.ten from kieu_dang tb where tb.deleted = 0
            """, nativeQuery = true)
    List<AdminAttributeSaleModuleResponse> getStyles();

    @Query(value = """
                select tb.id, tb.ten from tinh_nang tb where tb.deleted = 0
            """, nativeQuery = true)
    List<AdminAttributeSaleModuleResponse> getFeatures();

    @Query(value = """
                select sp.id, sp.ten, dm.ten as ten_danh_muc
                from san_pham sp
                join danh_muc dm on dm.id = sp.id_danh_muc
                where sp.deleted = 0 and sp.trang_thai = 'ACTIVE'
                and (:#{#req.key} is null or  sp.ten LIKE CONCAT('%', :#{#req.key}, '%'))
                and (:#{#req.idDanhMuc} is null or sp.id_danh_muc = :#{#req.idDanhMuc})
                
            """, nativeQuery = true)
    List<AdminProductSaleModuleResponse> getProducts(AdminFindProductSaleModuleRequest req);

    @Query(value = """
            select ROW_NUMBER() OVER(ORDER BY sp.id DESC) AS catalog,
            sp.id, sp.ten, dm.ten as ten_danh_muc, sum(spct.so_luong) as tong_so_luong
            from san_pham sp
            join danh_muc dm on dm.id = sp.id_danh_muc
            join san_pham_chi_tiet spct on sp.id = spct.id_san_pham
            where sp.deleted = 0 and sp.trang_thai = 'ACTIVE'
            and (:#{#req.key} is null or  sp.ten LIKE CONCAT('%', :#{#req.key}, '%'))
            and (:#{#req.idDanhMuc} is null or sp.id_danh_muc = :#{#req.idDanhMuc})
            group by sp.id, sp.ten, dm.ten
            having sum(spct.so_luong) > 0;
            """, countQuery = """
            select COUNT(sp.id)
            from san_pham sp
            join danh_muc dm on dm.id = sp.id_danh_muc
            join san_pham_chi_tiet spct on sp.id = spct.id_san_pham
            where sp.deleted = 0 and sp.trang_thai = 'ACTIVE'
            and (:#{#req.key} is null or  sp.ten LIKE CONCAT('%', :#{#req.key}, '%'))
            and (:#{#req.idDanhMuc} is null or sp.id_danh_muc = :#{#req.idDanhMuc})
            group by sp.id, sp.ten, dm.ten
            having sum(spct.so_luong) > 0;
            """, nativeQuery = true)
    Page<AdminProductSaleModuleResponse> getProducts(AdminFindProductSaleModuleRequest req, Pageable pageable);


    @Query(value = """
                select ROW_NUMBER() OVER(ORDER BY spct.id DESC) AS catalog,
                    spct.id,
                    spct.ma_san_pham_chi_tiet,
                    spct.gia,
                    tinh_gia_hien_tai(spct.id) as gia_hien_tai,
                    spct.ten,
                    CONCAT(sp.ten, ' ', dm.ten) as ten_san_pham,
                    th.ten as ten_thuong_hieu,
                    spct.gioi_tinh,
                    spct.so_luong,
                    kc.ten as kich_co,
                    coalesce(anh.url, 'default-product-detail-image-url.jpg') as link_anh,
                    CONCAT(ca.ten, ',', ta.ten, ',', ht.ten, ',', cl.ten, ',', kd.ten, ',', tn.ten, ',', ms.ma_mau_sac, ',', ms.ten) as phong_cach
                from san_pham_chi_tiet spct
                join san_pham sp on spct.id_san_pham = sp.id and sp.id in :#{#req.idSanPhams}
                join danh_muc dm on dm.id = sp.id_danh_muc
                join thuong_hieu th on spct.id_thuong_hieu = th.id
                join kich_co kc on spct.id_kich_co = kc.id
                left join co_ao ca on spct.id_co_ao = ca.id
                left join tay_ao ta on spct.id_tay_ao = ta.id
                left join hoa_tiet ht on spct.id_hoa_tiet = ht.id
                left join chat_lieu cl on spct.id_chat_lieu = cl.id
                left join kieu_dang kd on spct.id_kieu_dang = kd.id
                left join tinh_nang tn on spct.id_tinh_nang = tn.id
                join mau_sac ms on spct.id_mau_sac = ms.id
                left join anh on spct.id = anh.id_san_pham_chi_tiet and (anh.is_top = true)
                where spct.deleted = false
                and spct.trang_thai = 0
                and (:#{#req.gioiTinh} is null or spct.gioi_tinh = :#{#req.gioiTinh})
                and (:#{#req.keyword} is null
                    or sp.ten LIKE CONCAT('%', :#{#req.keyword}, '%')
                    or dm.ten LIKE CONCAT('%', :#{#req.keyword}, '%')
                    or spct.ma_san_pham_chi_tiet LIKE CONCAT('%', :#{#req.keyword}, '%')
                    or spct.ten LIKE CONCAT('%', :#{#req.keyword}, '%')
                    or ms.ten LIKE CONCAT('%', :#{#req.keyword}, '%'))
                and (:#{#req.idThuongHieu} is null or spct.id_thuong_hieu = :#{#req.idThuongHieu})
                and (:#{#req.idKichCo} is null or spct.id_kich_co = :#{#req.idKichCo})
                and (:#{#req.idCoAo} is null or spct.id_co_ao = :#{#req.idCoAo})
                and (:#{#req.idTayAo} is null or spct.id_tay_ao = :#{#req.idTayAo})
                and (:#{#req.idHoaTiet} is null or spct.id_hoa_tiet = :#{#req.idHoaTiet})
                and (:#{#req.idChatLieu} is null or spct.id_chat_lieu = :#{#req.idChatLieu})
                and (:#{#req.idTinhNang} is null or spct.id_tinh_nang = :#{#req.idTinhNang})
            """, countQuery = """
                select COUNT(spct.id)
                from san_pham_chi_tiet spct
                join san_pham sp on spct.id_san_pham = sp.id and sp.id in :#{#req.idSanPhams}
                join danh_muc dm on dm.id = sp.id_danh_muc
                join thuong_hieu th on spct.id_thuong_hieu = th.id
                join kich_co kc on spct.id_kich_co = kc.id
                left join co_ao ca on spct.id_co_ao = ca.id
                left join tay_ao ta on spct.id_tay_ao = ta.id
                left join hoa_tiet ht on spct.id_hoa_tiet = ht.id
                left join chat_lieu cl on spct.id_chat_lieu = cl.id
                left join kieu_dang kd on spct.id_kieu_dang = kd.id
                left join tinh_nang tn on spct.id_tinh_nang = tn.id
                join mau_sac ms on spct.id_mau_sac = ms.id
                where spct.deleted = false
                and spct.trang_thai = 0
                and (:#{#req.gioiTinh} is null or spct.gioi_tinh = :#{#req.gioiTinh})
                and (:#{#req.keyword} is null
                    or sp.ten LIKE CONCAT('%', :#{#req.keyword}, '%')
                    or dm.ten LIKE CONCAT('%', :#{#req.keyword}, '%')
                    or spct.ma_san_pham_chi_tiet LIKE CONCAT('%', :#{#req.keyword}, '%')
                    or spct.ten LIKE CONCAT('%', :#{#req.keyword}, '%')
                    or ms.ten LIKE CONCAT('%', :#{#req.keyword}, '%'))
                and (:#{#req.idThuongHieu} is null or spct.id_thuong_hieu = :#{#req.idThuongHieu})
                and (:#{#req.idKichCo} is null or spct.id_kich_co = :#{#req.idKichCo})
                and (:#{#req.idCoAo} is null or spct.id_co_ao = :#{#req.idCoAo})
                and (:#{#req.idTayAo} is null or spct.id_tay_ao = :#{#req.idTayAo})
                and (:#{#req.idHoaTiet} is null or spct.id_hoa_tiet = :#{#req.idHoaTiet})
                and (:#{#req.idChatLieu} is null or spct.id_chat_lieu = :#{#req.idChatLieu})
                and (:#{#req.idTinhNang} is null or spct.id_tinh_nang = :#{#req.idTinhNang})
            """, nativeQuery = true)
    Page<AdminProductDetailSaleModuleResponse> getProductDetailsByProductId(AdminFindProductDetailSaleModuleRequest req, Pageable pageable);


    @Query(value = """
             select
                    spct.id,
                    spct.ma_san_pham_chi_tiet,
                    spct.ten,
                    spct.gia,
                    tinh_gia_hien_tai(spct.id) as gia_hien_tai,
                    CONCAT(sp.ten, ' ', dm.ten) as ten_san_pham,
                    th.ten as ten_thuong_hieu,
                    spct.gioi_tinh,
                    spct.so_luong,
                    kc.ten as kich_co,
                    coalesce(anh.url, 'default-product-detail-image-url.jpg') as link_anh,
                    CONCAT(ca.ten, ',', ta.ten, ',', ht.ten, ',', cl.ten, ',', kd.ten, ',', tn.ten, ',', ms.ma_mau_sac, ',', ms.ten) as phong_cach
                from san_pham_chi_tiet spct
                join san_pham sp on spct.id_san_pham = sp.id
                join danh_muc dm on dm.id = sp.id_danh_muc
                join thuong_hieu th on spct.id_thuong_hieu = th.id
                join kich_co kc on spct.id_kich_co = kc.id
                left join co_ao ca on spct.id_co_ao = ca.id
                left join tay_ao ta on spct.id_tay_ao = ta.id
                left join hoa_tiet ht on spct.id_hoa_tiet = ht.id
                left join chat_lieu cl on spct.id_chat_lieu = cl.id
                left join kieu_dang kd on spct.id_kieu_dang = kd.id
                left join tinh_nang tn on spct.id_tinh_nang = tn.id
                join mau_sac ms on spct.id_mau_sac = ms.id
                left join anh on spct.id = anh.id_san_pham_chi_tiet and (anh.is_top = true)
                where spct.id = ?1
            """, nativeQuery = true)
    Optional<AdminProductDetailSaleModuleResponse> getProductDetailById(String id);

}
