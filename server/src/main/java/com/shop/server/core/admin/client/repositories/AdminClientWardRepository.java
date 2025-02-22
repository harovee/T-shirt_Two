package com.shop.server.core.admin.client.repositories;

import com.shop.server.repositories.WardRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminClientWardRepository extends WardRepository {
    @Query("SELECT w.name FROM Ward w WHERE w.code = :code")
    String getWardNameByCode(String code);
}
