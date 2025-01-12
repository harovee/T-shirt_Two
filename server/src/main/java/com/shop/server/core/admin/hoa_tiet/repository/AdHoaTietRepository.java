package com.shop.server.core.admin.hoa_tiet.repository;

import com.shop.server.core.admin.hoa_tiet.model.response.AdGetHoaTietResponse;
import com.shop.server.repositories.CoAoRepository;
import com.shop.server.repositories.HoaTietRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdHoaTietRepository extends HoaTietRepository {

    @Query(value = """
    SELECT ht.id AS id,
    ht.ten AS ten
    FROM HoaTiet ht
""")
    List<AdGetHoaTietResponse> getListHoaTiet ();

    Boolean existsHoaTietByMaHoaTiet(String maHoaTiet);

    Boolean existsHoaTietByTen(String ten);
}
