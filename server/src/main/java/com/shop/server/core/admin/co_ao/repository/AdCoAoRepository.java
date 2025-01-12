package com.shop.server.core.admin.co_ao.repository;

import com.shop.server.core.admin.co_ao.model.response.AdGetCoAoResponse;
import com.shop.server.repositories.CoAoRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdCoAoRepository extends CoAoRepository {

    @Query(value = """
    SELECT ca.id AS id,
    ca.ten AS ten
    FROM CoAo ca
""")
    List<AdGetCoAoResponse> getListCoAo ();

    Boolean existsCoAoByMaCoAo(String maCoAo);

    Boolean existsCoAoByTen(String ten);
}
