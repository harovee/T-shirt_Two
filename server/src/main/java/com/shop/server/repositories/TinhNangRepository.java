package com.shop.server.repositories;

import com.shop.server.entities.main.TinhNang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TinhNangRepository extends JpaRepository<TinhNang, String> {
}
