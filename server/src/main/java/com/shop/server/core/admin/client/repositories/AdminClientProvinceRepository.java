package com.shop.server.core.admin.client.repositories;

import com.shop.server.repositories.ProvinceRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminClientProvinceRepository extends ProvinceRepository {
    @Query("SELECT p.name FROM Province p WHERE p.id = :id")
    String getProvinceById(String id);
}
