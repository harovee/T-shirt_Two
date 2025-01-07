package com.shop.server.core.admin.client.repositories;

import com.shop.server.core.admin.client.models.requests.ClientFindProductRequest;
import com.shop.server.core.admin.client.models.responses.AdminClientResponse;
import com.shop.server.repositories.KhachHangRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminClientRepository extends KhachHangRepository {

    /**
     * Lấy dữ liệu khách hàng dựa trên các trường có trong request
     *
     * @param pageable
     * @param req
     * @return
     */
    @Query(value = """
                SELECT
                    ROW_NUMBER() OVER(ORDER BY kh.id DESC) AS catalog,
                	kh.id AS id,
                    kh.ho_va_ten as name,
                    kh.email as email,
                    kh.deleted as status
                FROM khach_hang kh
                WHERE
                    (:#{#req.keyword} IS NULL OR
                    kh.ho_va_ten LIKE CONCAT('%', :#{#req.keyword}, '%') OR
                    kh.email LIKE CONCAT('%', :#{#req.keyword}, '%'))
                AND (:#{#req.status} IS NULL OR kh.deleted = :#{#req.status})
            """, countQuery = """
                SELECT
                    COUNT(kh.id)
                FROM khach_hang kh
                WHERE
                    (:#{#req.keyword} IS NULL OR
                    kh.ho_va_ten LIKE CONCAT('%', :#{#req.keyword}, '%') OR
                    kh.email LIKE CONCAT('%', :#{#req.keyword}, '%'))
                AND (:#{#req.status} IS NULL OR kh.deleted = :#{#req.status})
            """, nativeQuery = true)
    Page<AdminClientResponse> getClientByRequest(Pageable pageable, ClientFindProductRequest req);

    boolean existsClientByEmail(String email);
}
