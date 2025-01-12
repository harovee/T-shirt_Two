package com.shop.server.core.admin.kieu_dang.repository;

import com.shop.server.core.admin.kieu_dang.model.response.AdGetKieuDangResponse;
import com.shop.server.repositories.CoAoRepository;
import com.shop.server.repositories.KieuDangRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdKieuDangRepository extends KieuDangRepository {

    @Query(value = """
    SELECT kd.id AS id,
    kd.ten AS ten
    FROM KieuDang kd
""")
    List<AdGetKieuDangResponse> getListKieuDang ();

    Boolean existsKieuDangByMaKieuDang(String maKieuDang);

    Boolean existsKieuDangByTen(String ten);
}
