package com.shop.server.core.admin.phieugiamgia.repository;

import com.shop.server.core.admin.phieugiamgia.model.request.AdminProductSearchRequest;
import com.shop.server.core.admin.phieugiamgia.model.response.AdminProductDetailResponse;
import com.shop.server.repositories.SanPhamChiTietRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdProductDetailRepository extends SanPhamChiTietRepository {

    @Query(value = """
            SELECT  spct.id as id,
                    spct.ma_san_pham_chi_tiet as maSPCT,
                    spct.ten as ten,
                    spct.gia as gia,
                    spct.so_luong as soLuong,
                    dm.ten as tenDanhMuc,
                    cl.ten as tenChatLieu,
                    th.ten as tenThuongHieu
                FROM  san_pham sp left join san_pham_chi_tiet spct on  sp.id = spct.id_san_pham
                                  join danh_muc dm on dm.id = sp.id_danh_muc
                                    join chat_lieu cl on cl.id = spct.id_chat_lieu
                                    join thuong_hieu th on th.id = spct.id_thuong_hieu
                WHERE 
                    sp.trang_thai = "Đang áp dụng"
    """, nativeQuery = true)
    Page<AdminProductDetailResponse> getAllProductDetail(Pageable pageable, AdminProductSearchRequest request );

    @Query(value = """
           SELECT  spct.id as id,
                    spct.ma_san_pham_chi_tiet as maSPCT,
                    spct.ten as ten,
                    spct.gia as gia,
                    spct.so_luong as soLuong,
                    dm.ten as tenDanhMuc,
                    cl.ten as tenChatLieu,
                    th.ten as tenThuongHieu
                FROM  san_pham sp left join san_pham_chi_tiet spct on  sp.id = spct.id_san_pham
                                  join danh_muc dm on dm.id = sp.id_danh_muc
                                    join chat_lieu cl on cl.id = spct.id_chat_lieu
                                    join thuong_hieu th on th.id = spct.id_thuong_hieu
                WHERE  spct.id = :#{#id}
                 AND  sp.trang_thai = "Đang áp dụng"
""", nativeQuery = true)
    Optional<AdminProductDetailResponse> getProductDetailById(String id);
}
