package com.shop.server.infrastructure.security.repository;

import com.shop.server.entities.main.KhachHang;
import com.shop.server.repositories.KhachHangRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SecurityClientRepository extends KhachHangRepository {

    Optional<KhachHang> findByEmail(String email);

    Optional<KhachHang> findByPhoneNumber(String phoneNumber);

    Optional<KhachHang> getKhachHangByEmailOrPhoneNumber(String email, String phoneNumber);

    boolean existsKhachHangById(String id);

}
