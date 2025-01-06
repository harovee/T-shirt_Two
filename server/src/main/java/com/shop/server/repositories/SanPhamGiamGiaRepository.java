package com.shop.server.repositories;

import com.shop.server.entities.main.SanPhamGiamGia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SanPhamGiamGiaRepository extends JpaRepository<SanPhamGiamGia, String> {
}
