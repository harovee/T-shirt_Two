package com.shop.server.core.admin.product.repositories;

import com.shop.server.core.admin.product.models.requests.AdminFindProductRequest;
import com.shop.server.core.admin.product.models.responses.AdminProductResponse;
import com.shop.server.entities.main.SanPham;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminProductRepository extends JpaRepository<SanPham, String> {

    /**
     * Lấy dữ liệu product dựa trên các trường có trong request
     * @param pageable
     * @param req
     * @return
     */
    @Query(value = """
                SELECT
                    ROW_NUMBER() OVER(ORDER BY p.id DESC) AS catalog,
                	p.name AS name,
                    p.description AS description
                FROM product p
            """, countQuery = """
                SELECT
                    COUNT(p.id)
                FROM product p
            """, nativeQuery = true)
    Page<AdminProductResponse> getProductsByRequest(Pageable pageable, AdminFindProductRequest req);

}
