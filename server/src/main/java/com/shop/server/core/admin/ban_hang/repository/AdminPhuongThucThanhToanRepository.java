package com.shop.server.core.admin.ban_hang.repository;

import com.shop.server.core.admin.ban_hang.model.response.AdminPhuongThucThanhToanResponse;
import com.shop.server.entities.main.PhuongThucThanhToan;
import com.shop.server.repositories.PhuongThucThanhToanRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminPhuongThucThanhToanRepository extends PhuongThucThanhToanRepository {
    @Query(value = """
            SELECT pttt.id as id,
                   pttt.ten_phuong_thuc as ten
            FROM phuong_thuc_thanh_toan pttt
""",nativeQuery = true)
    List<AdminPhuongThucThanhToanResponse> getAllPhuongThucThanhToan();
}
