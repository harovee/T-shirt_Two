package com.shop.server.repositories;

import com.shop.server.entities.main.VoucherSanPhamKhachHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface VoucherSanPhamKhachHangRepository extends JpaRepository<VoucherSanPhamKhachHang, String> {
}
