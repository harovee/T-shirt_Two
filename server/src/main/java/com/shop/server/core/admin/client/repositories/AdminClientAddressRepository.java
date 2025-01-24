package com.shop.server.core.admin.client.repositories;

import com.shop.server.entities.main.KhachHang;
import com.shop.server.repositories.AddressRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminClientAddressRepository extends AddressRepository {

    @Query(value = """
            SELECT EXISTS (
                    SELECT 1
                    FROM dia_chi_khach_hang dckh
                    WHERE dckh.id_khach_hang = :clientId
                )
            """, nativeQuery = true)
    Long existsAddressByClientId(String clientId);

}
