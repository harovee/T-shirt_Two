package com.shop.server.repositories;

import com.shop.server.entities.main.KhachHangPhieuGiamGia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KhachHangPhieuGiamGiaRepository extends JpaRepository<KhachHangPhieuGiamGia,String> {
}
