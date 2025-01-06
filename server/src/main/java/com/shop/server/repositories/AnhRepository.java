package com.shop.server.repositories;

import com.shop.server.entities.main.Anh;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnhRepository extends JpaRepository<Anh, String> {
}
