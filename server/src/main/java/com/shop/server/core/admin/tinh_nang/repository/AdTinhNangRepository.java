package com.shop.server.core.admin.tinh_nang.repository;

import com.shop.server.core.admin.tinh_nang.model.response.AdGetTinhNangResponse;
import com.shop.server.repositories.CoAoRepository;
import com.shop.server.repositories.TinhNangRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdTinhNangRepository extends TinhNangRepository {

    @Query(value = """
    SELECT tn.id AS id,
    tn.ten AS ten
    FROM TinhNang tn
""")
    List<AdGetTinhNangResponse> getListTinhNang ();

    Boolean existsTinhNangByMaTinhNang(String maTinhNang);

    Boolean existsTinhNangByTen(String ten);
}
