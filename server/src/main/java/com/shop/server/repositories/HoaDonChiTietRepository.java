package com.shop.server.repositories;

import com.shop.server.entities.main.HoaDon;
import com.shop.server.entities.main.HoaDonChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HoaDonChiTietRepository extends JpaRepository<HoaDonChiTiet, String> {

}
