package com.shop.server.core.admin.point_of_sale.repository;

import com.shop.server.entities.main.SanPhamChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PointOfSaleRepository extends JpaRepository<SanPhamChiTiet, String> {
}
