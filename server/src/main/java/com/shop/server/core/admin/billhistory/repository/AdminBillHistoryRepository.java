package com.shop.server.core.admin.billhistory.repository;

import com.shop.server.core.admin.billhistory.model.request.AdminFindBillHistoryRequest;
import com.shop.server.core.admin.billhistory.model.response.AdminBillHistoryResponse;
import com.shop.server.repositories.LichSuHoaDonRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminBillHistoryRepository extends LichSuHoaDonRepository {
//    @Query(value = """
//        SELECT
//            ROW_NUMBER() OVER (ORDER BY ls.id DESC) AS catalog,
//            ls.id AS id,
//            hd.ma_hoa_don AS maHoaDon,
//            ls.hanh_dong AS hanhDong,
//            ls.mo_ta AS moTa,
//            ls.ngay_tao AS ngayTao,
//            ls.ngay_sua AS ngaySua,
//            ls.nguoi_tao AS nguoiTao,
//            ls.nguoi_sua AS nguoiSua,
//            ls.trang_thai AS trangThai
//            FROM lich_su_hoa_don ls
//            JOIN hoa_don hd ON hd.id = ls.id_hoa_don
//            WHERE
//                (:#{#req.idHoaDon} IS NULL OR :#{#req.idHoaDon} = ls.id_hoa_don)
//        """, countQuery = """
//            SELECT
//                COUNT(ls.id)
//            FROM lich_su_hoa_don ls
//            JOIN hoa_don hd ON hd.id = ls.id_hoa_don
//            WHERE
//                (:#{#req.idHoaDon} IS NULL OR :#{#req.idHoaDon} = ls.id_hoa_don)
//        """, nativeQuery = true)
//    Page<AdminBillHistoryResponse> getAdminBillHistoryByRequest(Pageable pageable,
//                                                       AdminFindBillHistoryRequest req);

    @Query(value = """
        SELECT
            ROW_NUMBER() OVER (ORDER BY ls.ngay_tao DESC) AS catalog,
            ls.id AS id,
            hd.ma_hoa_don AS maHoaDon,
            ls.hanh_dong AS hanhDong,
            ls.mo_ta AS moTa,
            ls.ngay_tao AS ngayTao,
            CONCAT(nv.sub_code,nv.ma_nhan_vien) AS nguoiTao,
            ls.trang_thai AS trangThai
            FROM lich_su_hoa_don ls
            JOIN hoa_don hd ON hd.id = ls.id_hoa_don
            LEFT JOIN nhan_vien nv ON nv.id = ls.nguoi_tao
            WHERE
                (:#{#req.idHoaDon} IS NULL OR :#{#req.idHoaDon} = ls.id_hoa_don)
            ORDER BY ls.ngay_tao
        """, countQuery = """
            SELECT
                COUNT(ls.id)
            FROM lich_su_hoa_don ls
            JOIN hoa_don hd ON hd.id = ls.id_hoa_don
            LEFT JOIN nhan_vien nv ON nv.id = ls.nguoi_tao
            WHERE
                (:#{#req.idHoaDon} IS NULL OR :#{#req.idHoaDon} = ls.id_hoa_don)
        """, nativeQuery = true)
    List<AdminBillHistoryResponse> getAllAdminBillHistory(AdminFindBillHistoryRequest req);
}
