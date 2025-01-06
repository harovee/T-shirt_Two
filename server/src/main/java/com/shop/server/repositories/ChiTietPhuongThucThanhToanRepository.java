package com.shop.server.repositories;

import com.shop.server.entities.main.ChiTietPhuongThucThanhToan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChiTietPhuongThucThanhToanRepository extends JpaRepository<ChiTietPhuongThucThanhToan, String> {
}
