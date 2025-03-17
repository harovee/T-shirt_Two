package com.shop.server.core.client.payment.repository;

import com.shop.server.entities.main.PhuongThucThanhToan;
import com.shop.server.repositories.PhuongThucThanhToanRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientPhuongThucThanhToanRepository extends PhuongThucThanhToanRepository {

    @Query(value = """
            SELECT *
            FROM phuong_thuc_thanh_toan pttt
            WHERE pttt.ten_phuong_thuc LIKE :#{#name}
"""
            ,nativeQuery = true   )
    PhuongThucThanhToan findPhuongThucThanhToanByName(String name);
}