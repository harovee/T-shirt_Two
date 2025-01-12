package com.shop.server.core.admin.mau_sac.repository;

import com.shop.server.core.admin.mau_sac.model.response.AdGetMauSacResponse;
import com.shop.server.repositories.CoAoRepository;
import com.shop.server.repositories.MauSacRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdMauSacRepository extends MauSacRepository {

    @Query(value = """
    SELECT ms.id AS id,
    ms.ten AS ten
    FROM MauSac ms
""")
    List<AdGetMauSacResponse> getListMauSac ();

    Boolean existsMauSacByMaMauSac(String maMauSac);

    Boolean existsMauSacByTen(String ten);
}
