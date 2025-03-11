package com.shop.server.core.client.my_page.repository;

import com.shop.server.core.admin.bill.model.request.AdminFindBillRequest;
import com.shop.server.core.admin.bill.model.response.AdminBillResponse;
import com.shop.server.core.client.my_page.model.request.ClientBillRequest;
import com.shop.server.core.client.my_page.model.response.ClientBillResponse;
import com.shop.server.repositories.HoaDonRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientBillRepository extends HoaDonRepository {

    @Query(value = """
        SELECT
            ROW_NUMBER() OVER(ORDER BY hd.ngay_tao ) AS catalog,
            hd.id AS id,
            hd.ma_hoa_don AS ma,
            hd.tong_tien AS tongTien,
            hd.tien_giam AS tienGiam,
            pg.gia_tri_giam AS giaTriGiam,
            hd.tien_ship AS phiShip,
            COALESCE(SUM(ctpttt.tien_khach_dua), 0) AS daTra,
            ((hd.tong_tien) - COALESCE(SUM(ctpttt.tien_khach_dua), 0)) AS tienPhaiTra,
            hd.trang_thai AS trangThai,
            hd.loai_hoa_don AS loaiDon,
            hd.phuong_thuc_nhan AS phuongThucNhan
        FROM hoa_don hd
        LEFT JOIN phieu_giam_gia pg ON hd.id_phieu_giam_gia = pg.id
        LEFT JOIN chi_tiet_phuong_thuc_thanh_toan ctpttt ON hd.id = ctpttt.id_hoa_don
        WHERE
            (:#{#req.keyword} IS NULL OR
            hd.ma_hoa_don LIKE CONCAT('%', :#{#req.keyword},'%'))
         AND
            ((:#{#req.trangThai} IS NULL OR
            hd.trang_thai LIKE CONCAT('%', :#{#req.trangThai},'%')))
         AND hd.trang_thai NOT LIKE 'Hóa đơn chờ'
        AND hd.id_khach_hang = :#{#req.idKhachHang}
        GROUP BY hd.id, hd.ma_hoa_don, hd.tong_tien, hd.loai_hoa_don, hd.tien_giam, pg.gia_tri_giam,
                 hd.tien_ship, hd.trang_thai, hd.phuong_thuc_nhan
     """, countQuery = """
        SELECT COUNT(hd.id)
        FROM hoa_don hd
        WHERE
            (:#{#req.keyword} IS NULL OR
            hd.ma_hoa_don LIKE CONCAT('%', :#{#req.keyword},'%'))
        AND 
            ((:#{#req.trangThai} IS NULL OR
            hd.trang_thai LIKE CONCAT('%', :#{#req.trangThai},'%')))
        AND hd.trang_thai NOT LIKE 'Hóa đơn chờ'
        AND hd.id_khach_hang = :#{#req.idKhachHang}
    """, nativeQuery = true)
    Page<ClientBillResponse> getAllBillsByRequest(Pageable pageable, ClientBillRequest req);

}
