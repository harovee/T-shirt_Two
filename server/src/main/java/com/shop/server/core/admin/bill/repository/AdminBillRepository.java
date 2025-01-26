package com.shop.server.core.admin.bill.repository;

import com.shop.server.core.admin.bill.model.request.AdminFindBillRequest;
import com.shop.server.core.admin.bill.model.response.AdminBillResponse;
import com.shop.server.repositories.HoaDonRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminBillRepository extends HoaDonRepository {

    @Query(value = """
        SELECT
            ROW_NUMBER() OVER(ORDER BY hd.id) AS catalog,
            hd.id AS id,
            hd.ma_hoa_don AS ma,
            hd.tong_tien AS tongTien,
            hd.loai_hoa_don AS loaiHD,
            hd.ten_nguoi_nhan AS tenNguoiNhan,
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
            (:#{#req.trangThai} IS NULL OR
            hd.trang_thai LIKE CONCAT('%', :#{#req.trangThai},'%'))
         AND
            (:#{#req.ngayBatDau} IS NULL OR :#{#req.ngayKetThuc} IS NULL OR
            (hd.ngay_tao BETWEEN :#{#req.ngayBatDau} AND :#{#req.ngayKetThuc}))
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
            (:#{#req.trangThai} IS NULL OR
            hd.trang_thai LIKE CONCAT('%', :#{#req.trangThai},'%'))
        AND
            (:#{#req.ngayBatDau} IS NULL OR :#{#req.ngayKetThuc} IS NULL OR
            (hd.ngay_tao BETWEEN :#{#req.ngayBatDau} AND :#{#req.ngayKetThuc}))
         AND
            (:#{#req.loaiHD} IS NULL OR
            hd.loai_hoa_don LIKE CONCAT('%', :#{#req.loaiHD},'%'))
    """, nativeQuery = true)
    Page<AdminBillResponse> getBillsByRequest(Pageable pageable, AdminFindBillRequest req);

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
            hd.trang_thai AS trangThaiHD,
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
}
