package com.shop.server.infrastructure.security.repository;

import com.shop.server.entities.Staff;
import com.shop.server.repositories.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SecurityUserRepository extends UserRepository {

    Optional<Staff> findByEmail(String email);

}
