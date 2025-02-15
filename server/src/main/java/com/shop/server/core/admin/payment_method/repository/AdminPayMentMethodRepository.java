package com.shop.server.core.admin.payment_method.repository;

import com.shop.server.core.admin.payment_method.model.response.AdminPaymentMethodResponse;
import com.shop.server.repositories.PhuongThucThanhToanRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminPayMentMethodRepository extends PhuongThucThanhToanRepository {
    @Query(value = """
    SELECT
        pt.id AS id,
        pt.ten_phuong_thuc AS tenPhuongThuc
        FROM phuong_thuc_thanh_toan pt
    """, nativeQuery = true)
    List<AdminPaymentMethodResponse> getAllPaymentMethods();
}
