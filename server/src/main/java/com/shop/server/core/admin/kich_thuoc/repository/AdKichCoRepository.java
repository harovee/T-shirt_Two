package com.shop.server.core.admin.kich_thuoc.repository;

import com.shop.server.core.admin.kich_thuoc.model.response.AdGetKichCoResponse;
import com.shop.server.repositories.CoAoRepository;
import com.shop.server.repositories.KichCoRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdKichCoRepository extends KichCoRepository {

    @Query(value = """
    SELECT kc.id AS id,
    kc.ten AS ten
    FROM KichCo kc
""")
    List<AdGetKichCoResponse> getListKichCo ();

    Boolean existsKichCoByMaKichCo(String maKichCo);

    Boolean existsKichCoByTen(String ten);
}
