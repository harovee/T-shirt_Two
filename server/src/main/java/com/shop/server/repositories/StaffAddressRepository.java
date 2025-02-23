package com.shop.server.repositories;

import com.shop.server.entities.main.StaffAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StaffAddressRepository extends JpaRepository<StaffAddress, String> {
}
