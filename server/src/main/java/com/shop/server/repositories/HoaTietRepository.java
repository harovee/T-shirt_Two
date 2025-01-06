package com.shop.server.repositories;

import com.shop.server.entities.main.HoaTiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HoaTietRepository extends JpaRepository<HoaTiet, String> {
}
