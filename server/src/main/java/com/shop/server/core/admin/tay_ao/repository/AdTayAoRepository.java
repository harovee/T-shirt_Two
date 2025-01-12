package com.shop.server.core.admin.tay_ao.repository;

import com.shop.server.core.admin.tay_ao.model.response.AdGetTayAoResponse;
import com.shop.server.repositories.CoAoRepository;
import com.shop.server.repositories.TayAoRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdTayAoRepository extends TayAoRepository {

    @Query(value = """
    SELECT ta.id AS id,
    ta.ten AS ten
    FROM TayAo ta
""")
    List<AdGetTayAoResponse> getListTayAo ();

    Boolean existsTayAoByMaTayAo(String maTayAo);

    Boolean existsTayAoByTen(String ten);
}
