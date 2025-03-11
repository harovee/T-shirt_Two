package com.shop.server.core.client.product.repository;

import com.shop.server.core.client.product.model.response.thuoc_tinh.ChatLieuResponse;
import com.shop.server.core.client.product.model.response.thuoc_tinh.ColorResponse;
import com.shop.server.core.client.product.model.response.thuoc_tinh.DanhMucResponse;
import com.shop.server.core.client.product.model.response.thuoc_tinh.KieuDangResponse;
import com.shop.server.core.client.product.model.response.thuoc_tinh.ThuongHieuResponse;
import com.shop.server.repositories.DanhMucRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClientThuocTinhRepository extends DanhMucRepository {

    @Query(value = """
            SELECT dm.id,
            dm.ten
            FROM danh_muc dm
    """,nativeQuery = true)
    List<DanhMucResponse> getDanhMuc();

    @Query(value = """
            SELECT dm.id,
            dm.ten
            FROM chat_lieu dm
    """,nativeQuery = true)
    List<ChatLieuResponse> getChatLieu();

    @Query(value = """
            SELECT dm.id,
            dm.ten
            FROM kieu_dang dm
    """,nativeQuery = true)
    List<KieuDangResponse> getKieuDang();

    @Query(value = """
            SELECT dm.id,
            dm.ten
            FROM thuong_hieu dm
    """,nativeQuery = true)
    List<ThuongHieuResponse> getThuongHieu();

    @Query(value = """
            SELECT  dm.id,
                    dm.ma_mau_sac,
                    dm.ten   
            FROM mau_sac dm
    """,nativeQuery = true)
    List<ColorResponse> getColor();

}
