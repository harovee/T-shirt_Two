package com.shop.server.repositories;

import com.shop.server.entities.main.SanPhamChiTiet;
import com.shop.server.entities.main.SanPhamGiamGia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SanPhamGiamGiaRepository extends JpaRepository<SanPhamGiamGia, String> {
    List<SanPhamGiamGia> findBySanPhamChiTiet(SanPhamChiTiet sanPhamChiTiet);
}
