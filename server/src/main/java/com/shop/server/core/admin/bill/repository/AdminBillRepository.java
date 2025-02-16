package com.shop.server.core.admin.bill.repository;

import com.shop.server.core.admin.bill.model.request.AdminFindBillRequest;
import com.shop.server.core.admin.bill.model.response.AdminBillResponse;
import com.shop.server.core.admin.bill.model.response.AdminBillWaitResponse;
import com.shop.server.repositories.HoaDonRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminBillRepository extends HoaDonRepository {

    @Query(value = """
        SELECT
            ROW_NUMBER() OVER(ORDER BY hd.ngay_tao ) AS catalog,
            hd.id AS id,
            hd.ma_hoa_don AS ma,
            hd.tong_tien AS tongTien,
            hd.loai_hoa_don AS loaiHD,
            hd.ten_nguoi_nhan AS tenNguoiNhan,
            hd.dia_chi_nguoi_nhan AS diaChiNguoiNhan,
            hd.tien_giam AS tienGiam,
            hd.tien_ship AS tienShip,
            hd.ngay_ship AS ngayShip,
            hd.ghi_chu AS ghiChu,
            kh.so_dien_thoai AS soDienThoai,
            hd.trang_thai AS trangThai,
            hd.ngay_tao AS ngayTao,
            nv.ma_nhan_vien AS maNhanVien,
            kh.ho_va_ten AS tenKhachHang
        FROM hoa_don hd
        LEFT JOIN khach_hang kh ON hd.id_khach_hang = kh.id
        LEFT JOIN nhan_vien nv ON hd.id_nhan_vien = nv.id
        LEFT JOIN phieu_giam_gia pg ON hd.id_phieu_giam_gia = pg.id
        WHERE
            (:#{#req.keyword} IS NULL OR
            hd.ma_hoa_don LIKE CONCAT('%', :#{#req.keyword},'%') OR
            kh.ho_va_ten LIKE CONCAT('%', :#{#req.keyword},'%') OR
            kh.so_dien_thoai LIKE CONCAT('%', :#{#req.keyword},'%') OR
            nv.ho_va_ten LIKE CONCAT('%', :#{#req.keyword},'%'))
         AND
            ((:#{#req.trangThai} IS NULL OR
            hd.trang_thai LIKE CONCAT('%', :#{#req.trangThai},'%')))
            AND hd.trang_thai NOT LIKE 'Hóa đơn chờ'
         AND
            (
                (:#{#req.ngayBatDau} IS NULL OR hd.ngay_tao >= :#{#req.ngayBatDau}) AND
                (:#{#req.ngayKetThuc} IS NULL OR hd.ngay_tao <= :#{#req.ngayKetThuc})
            )
         AND
            (:#{#req.loaiHD} IS NULL OR
            hd.loai_hoa_don LIKE CONCAT('%', :#{#req.loaiHD},'%'))
     """, countQuery = """
        SELECT COUNT(hd.id)
        FROM hoa_don hd
        LEFT JOIN khach_hang kh ON hd.id_khach_hang = kh.id
        LEFT JOIN nhan_vien nv ON hd.id_nhan_vien = nv.id
        WHERE
            (:#{#req.keyword} IS NULL OR
            hd.ma_hoa_don LIKE CONCAT('%', :#{#req.keyword},'%') OR
            kh.ho_va_ten LIKE CONCAT('%', :#{#req.keyword},'%') OR
            kh.so_dien_thoai LIKE CONCAT('%', :#{#req.keyword},'%') OR
            nv.ho_va_ten LIKE CONCAT('%', :#{#req.keyword},'%'))
        AND 
            ((:#{#req.trangThai} IS NULL OR
            hd.trang_thai LIKE CONCAT('%', :#{#req.trangThai},'%')))
            AND hd.trang_thai NOT LIKE 'Hóa đơn chờ'
        AND
            (
                (:#{#req.ngayBatDau} IS NULL OR hd.ngay_tao >= :#{#req.ngayBatDau}) AND
                (:#{#req.ngayKetThuc} IS NULL OR hd.ngay_tao <= :#{#req.ngayKetThuc})
            )
         AND
            (:#{#req.loaiHD} IS NULL OR
            hd.loai_hoa_don LIKE CONCAT('%', :#{#req.loaiHD},'%'))
    """, nativeQuery = true)
    Page<AdminBillResponse> getBillsByRequest(Pageable pageable, AdminFindBillRequest req);

    @Query(value = """
            SELECT
                hd.id AS id,
                hd.ma_hoa_don AS ma,
                hd.tong_tien AS tongTien,
                hd.tien_giam AS tienGiam,
                hd.tien_ship AS tienShip,
                hd.loai_hoa_don AS loaiHD,
                hd.ten_nguoi_nhan AS tenNguoiNhan,
                hd.so_dien_thoai AS soDienThoai,
                hd.ghi_chu AS ghiChu,
                hd.trang_thai AS trangThai,
                nv.id AS idNhanVien,
                kh.id AS idKhachHang,
                pg.id AS idPhieuGiamGia,
                hd.ngay_tao AS ngayTao,
                hd.dia_chi_nguoi_nhan AS diaChiNguoiNhan
            FROM hoa_don hd
            LEFT JOIN khach_hang kh ON hd.id_khach_hang = kh.id
            LEFT JOIN nhan_vien nv ON hd.id_nhan_vien = nv.id
            LEFT JOIN phieu_giam_gia pg ON hd.id_phieu_giam_gia = pg.id
            WHERE
                hd.trang_thai = 'Hóa đơn chờ'
            AND
                hd.loai_hoa_don = 'Tại quầy'
            """, nativeQuery = true)
    List<AdminBillWaitResponse> getBillsWait();

    @Query(value = """
        SELECT
            hd.id AS id,
            hd.ma_hoa_don AS ma,
            hd.tien_giam AS tienGiam,
            hd.tien_ship AS tienShip,
            hd.tong_tien AS tongTien,
            hd.loai_hoa_don AS loaiHD,
            hd.ten_nguoi_nhan AS tenNguoiNhan,
            hd.so_dien_thoai AS soDienThoai,
            hd.dia_chi_nguoi_nhan AS diaChiNguoiNhan,
            hd.tien_ship AS tienShip,
            hd.ngay_ship AS ngayShip,
            hd.ghi_chu AS ghiChu,
            hd.trang_thai AS trangThai,
            nv.ma_nhan_vien AS maNhanVien,
            kh.ho_va_ten AS tenKhachHang,
            pg.ten AS tenPhieuGiamGia
        FROM hoa_don hd
        LEFT JOIN khach_hang kh ON hd.id_khach_hang = kh.id
        LEFT JOIN nhan_vien nv ON hd.id_nhan_vien = nv.id
        LEFT JOIN phieu_giam_gia pg ON hd.id_phieu_giam_gia = pg.id
        WHERE hd.id = :id
    """, nativeQuery = true)
    AdminBillResponse getDetailBillById(String id);

    boolean existsHoaDonByMa(String ma);

    @Query(value = """
        SELECT hd.trang_thai AS trangThai, COUNT(hd.id) AS count
        FROM hoa_don hd
        GROUP BY hd.trang_thai
    """, nativeQuery = true)
    List<Object[]> countBillsByStatus();

    @Modifying
    @Query(value = "DELETE FROM hoa_don_chi_tiet h WHERE h.id_hoa_don = :idHoaDon", nativeQuery = true)
    void deleteByIdHoaDon(@Param("idHoaDon") String idHoaDon);
}
