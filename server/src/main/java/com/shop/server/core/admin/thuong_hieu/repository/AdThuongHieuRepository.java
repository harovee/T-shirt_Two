package com.shop.server.core.admin.thuong_hieu.repository;

import com.shop.server.core.admin.thuong_hieu.model.response.AdGetThuongHieuResponse;
import com.shop.server.repositories.CoAoRepository;
import com.shop.server.repositories.ThuongHieuRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdThuongHieuRepository extends ThuongHieuRepository {

    @Query(value = """
    SELECT th.id AS id,
    th.ten AS ten
    FROM ThuongHieu th
""")
    List<AdGetThuongHieuResponse> getListThuongHieu ();

    Boolean existsThuongHieuByMaThuongHieu(String maThuongHieu);

    Boolean existsThuongHieuByTen(String ten);
}
