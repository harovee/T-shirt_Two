package com.shop.server.core.admin.client.repositories;

import com.shop.server.repositories.DistrictRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminClientDistrictRepository extends DistrictRepository {
    @Query("SELECT d.name FROM District d WHERE d.id = :id")
    String getDistrictsById(String id);
}
