package com.shop.server.infrastructure.security.repository;

import com.shop.server.entities.main.Staff;
import com.shop.server.repositories.NhanVienRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SecurityNhanVienRepository extends NhanVienRepository {

    Optional<Staff> findByEmail(String email);

}
