package com.shop.server.core.admin.point_of_sale.repository;

import com.shop.server.core.admin.point_of_sale.model.request.AdPOSAddProductToCartRequest;
import com.shop.server.core.admin.point_of_sale.model.request.AdPOSAddProductsToCartRequest;
import com.shop.server.core.admin.point_of_sale.model.request.AdPOSUpdateCartRequest;
import com.shop.server.repositories.HoaDonChiTietRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface POSOrderDetailRepository extends HoaDonChiTietRepository {

    boolean existsByHoaDonIdAndSanPhamChiTietId(String idOrder, String idProductDetail);

    @Modifying
    @Transactional
    @Query(value = """
            insert into hoa_don_chi_tiet
            (id, ngay_tao, deleted, ngay_sua, nguoi_sua, nguoi_tao,
             gia, so_luong, thanh_tien,
             trang_thai, id_hoa_don, id_san_pham_chi_tiet)
             value (
                UUID(),
                UNIX_TIMESTAMP()*1000,
                false,
                UNIX_TIMESTAMP()*1000,
                :#{#req.userEmail},
                :#{#req.userEmail},
                tinh_gia_hien_tai(:#{#req.idSanPhamChiTiet}),
                0,
                tinh_gia_hien_tai(:#{#req.idSanPhamChiTiet})*:#{#req.soLuong},
                'PENDING',
                :#{#req.idHoaDonCho},
                :#{#req.idSanPhamChiTiet} );
                
                update san_pham_chi_tiet spct
                 set spct.so_luong = spct.so_luong - :#{#req.soLuong}
                 where spct.id = :#{#req.idSanPhamChiTiet}
                 
                
            """, nativeQuery = true)
    void addOneProductDetailToCart(AdPOSAddProductToCartRequest req);


    @Modifying
    @Transactional
    @Query(value = """
            
            update san_pham_chi_tiet spct
                set spct.so_luong = spct.so_luong + (select hdct.so_luong from hoa_don_chi_tiet hdct where hdct.id = :#{#req.idHoaDonChiTiet}) - :#{#req.soLuongBanSau}
                where spct.id =  (select hdct.id_san_pham_chi_tiet
                    from hoa_don_chi_tiet hdct
                    where hdct.id = :#{#req.idHoaDonChiTiet});
            """, nativeQuery = true)
    void updateQuantityProductDetail(AdPOSUpdateCartRequest req);


    @Modifying
    @Transactional
    @Query(value = """
            update hoa_don_chi_tiet hdct
                    set hdct.so_luong = :#{#req.soLuongBanSau},
                        hdct.thanh_tien = :#{#req.soLuongBanSau}*hdct.gia,
                        hdct.ngay_sua = UNIX_TIMESTAMP()*1000
                    where hdct.id = :#{#req.idHoaDonChiTiet};
                
                """, nativeQuery = true)
    void updateQuantityOrderDetail(AdPOSUpdateCartRequest req);


    @Modifying
    @Transactional
    @Query(value = """
            delete from hoa_don_chi_tiet where id = ?1;
            """, nativeQuery = true)
    void deleteProductInCart(String idOrderDetail);

    @Modifying
    @Transactional
    @Query(value = """
        update san_pham_chi_tiet spct
        set spct.so_luong = spct.so_luong + (select hdct.so_luong from hoa_don_chi_tiet hdct where hdct.id = ?1)
        where spct.id = (select hdct.id_san_pham_chi_tiet from hoa_don_chi_tiet hdct where hdct.id = ?1);
        """, nativeQuery = true)
    void updateProductQuantityAfterDelete(String idOrderDetail);




    @Modifying
    @Transactional
    @Query(value = """
            insert into hoa_don_chi_tiet
            (id, ngay_tao, deleted, ngay_sua, nguoi_sua, nguoi_tao,
             gia, so_luong, thanh_tien,
             trang_thai, id_hoa_don, id_san_pham_chi_tiet)
             select
                UUID(),
                UNIX_TIMESTAMP()*1000,
                false,
                UNIX_TIMESTAMP()*1000,
                :#{#req.userEmail},
                :#{#req.userEmail},
                if(tinh_gia_hien_tai(spct.id) is  null, spct.gia, tinh_gia_hien_tai(spct.id)),
                1,
                if(tinh_gia_hien_tai(spct.id) is  null, spct.gia, tinh_gia_hien_tai(spct.id)),
                'PENDING',
                :#{#req.idHoaDonCho},
                spct.id
             from san_pham_chi_tiet spct
             where spct.id in :#{#req.idSanPhamChiTiets}
                   and not exists (
                       select 1 from hoa_don_chi_tiet hdct
                            where hdct.id_hoa_don = :#{#req.idHoaDonCho}
                            and hdct.id_san_pham_chi_tiet = spct.id
                   );
            """, nativeQuery = true)
    void saveProductDetailsToCart(AdPOSAddProductsToCartRequest req);


    @Modifying
    @Transactional
    @Query(value = """
                 update hoa_don_chi_tiet hdct
                     set hdct.so_luong = hdct.so_luong + :#{#req.soLuong},
                         hdct.ngay_sua = UNIX_TIMESTAMP()*1000,
                         hdct.nguoi_sua = :#{#req.userEmail},
                         hdct.thanh_tien = hdct.gia * hdct.so_luong
                     where hdct.id_hoa_don = :#{#req.idHoaDonCho}
                     and hdct.id_san_pham_chi_tiet in :#{#req.idSanPhamChiTiets};
            """, nativeQuery = true)
    void updateExistingProductInCart(AdPOSAddProductsToCartRequest req);

    @Modifying
    @Transactional
    @Query(value = """
                update san_pham_chi_tiet spct
                    set spct.so_luong = spct.so_luong - :#{#quantity}
                    where spct.id in :#{#idSanPhamChiTiets};
            """, nativeQuery = true)
    void decreaseStock(List<String> idSanPhamChiTiets, Long quantity);

    @Query(value = "SELECT SUM(hdct.thanh_tien) FROM hoa_don_chi_tiet hdct WHERE hdct.id_hoa_don = :idHoaDon", nativeQuery = true)
    Double getTotalAmountByIdHoaDon(@Param("idHoaDon") String idHoaDon);
}
