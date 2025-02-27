package com.shop.server.infrastructure.security.repository;

import com.shop.server.entities.main.NhanVien;
import com.shop.server.repositories.NhanVienRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SecurityStaffRepository extends NhanVienRepository {

    Optional<NhanVien> findByEmail(String email);

    Optional<NhanVien> findByPhoneNumber(String phoneNumber);

    boolean existsNhanVienById(String id);

}
