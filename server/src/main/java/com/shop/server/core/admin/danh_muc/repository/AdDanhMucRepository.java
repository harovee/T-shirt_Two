package com.shop.server.core.admin.danh_muc.repository;

import com.shop.server.core.admin.danh_muc.model.response.AdGetDanhMucResponse;
import com.shop.server.repositories.DanhMucRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdDanhMucRepository extends DanhMucRepository {

    @Query(value = """
    SELECT dm.id AS id,
    dm.ten AS ten
    FROM DanhMuc dm
""")
    List<AdGetDanhMucResponse> getListDanhMuc ();

    Boolean existsDanhMucByMaDanhMuc(String maDanhMuc);

    Boolean existsDanhMucByTen(String ten);
}
