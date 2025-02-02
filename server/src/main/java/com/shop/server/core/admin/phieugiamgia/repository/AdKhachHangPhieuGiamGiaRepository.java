package com.shop.server.core.admin.phieugiamgia.repository;

import com.shop.server.core.admin.phieugiamgia.model.request.VoucherKhachHangRequest;
import com.shop.server.entities.main.KhachHangPhieuGiamGia;
import com.shop.server.repositories.KhachHangPhieuGiamGiaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdKhachHangPhieuGiamGiaRepository extends KhachHangPhieuGiamGiaRepository {

    @Modifying
    @Transactional
    @Query(value = """

            INSERT INTO khach_hang_phieu_giam_gia
                    (id, id_khach_hang, id_phieu_giam_gia, da_su_dung,
                    deleted, ngay_tao, nguoi_tao)
                    SELECT\s
                        UUID(),
                        kh.id,
                        :#{#req.idPhieuGiamGia},
                        false,
                        false, UNIX_TIMESTAMP()*1000, :#{#req.nhanVien}
                        FROM khach_hang kh
                        WHERE kh.id IN (:#{#req.idKhachHangs})
""",nativeQuery = true)
    void saveVoucherAndCustomerVoucher(VoucherKhachHangRequest request);

    @Query(value = """
        select * from khach_hang_phieu_giam_gia ph
        where ph.id_phieu_giam_gia = ?1
""", nativeQuery = true)
    List<KhachHangPhieuGiamGia> findByIdPhieuGiamGia(String idPhieuGiamGia);

    @Query(value = """
            SELECT * 
            FROM khach_hang_phieu_giam_gia ph 
            WHERE ph.id_phieu_giam_gia = :#{#idPhieuGiamGia} and ph.id_khach_hang = :#{#idKhachHang}
            AND (ph.deleted = 0  OR ph.deleted is null )
""",nativeQuery = true)
    Optional<KhachHangPhieuGiamGia> findKhachHangIdAndPhieuGiamGiaId(String idKhachHang, String idPhieuGiamGia);
}
