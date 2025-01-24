package com.shop.server.core.admin.billhistory.repository;

import com.shop.server.core.admin.billhistory.model.request.AdminFindPayHistoryRequest;
import com.shop.server.core.admin.billhistory.model.response.AdminPayHistoryResponse;
import com.shop.server.entities.main.ChiTietPhuongThucThanhToan;
import com.shop.server.repositories.ChiTietPhuongThucThanhToanRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminPayHistoryRepository extends ChiTietPhuongThucThanhToanRepository {
    @Query(value = """
    SELECT
        cttt.ma_giao_dich AS maGiaoDich,
        pt.ten_phuong_thuc  tenPhuongThuc,
        cttt.ghi_chu AS ghiChu,
        cttt.ngay_tao AS ngayTao,
        cttt.nguoi_tao AS nguoiTao,
        hd.tong_tien AS tongTienHD
        FROM chi_tiet_phuong_thuc_thanh_toan cttt
        JOIN phuong_thuc_thanh_toan pt ON cttt.id_phuong_thuc_thanh_toan = pt.id
        JOIN hoa_don hd ON cttt.id_hoa_don = hd.id
        WHERE
            (:#{#req.idHoaDon} IS NULL OR :#{#req.idHoaDon} = cttt.id_hoa_don)
    """, nativeQuery = true)
    List<AdminPayHistoryResponse> getPayHistory(AdminFindPayHistoryRequest req);
}
