package com.shop.server.core.admin.phieugiamgia.repository;

import com.nimbusds.openid.connect.sdk.assurance.evidences.Voucher;
import com.shop.server.core.admin.phieugiamgia.model.request.VoucherSanPhamKhachHangRequest;
import com.shop.server.core.admin.phieugiamgia.model.response.AdminVoucherSanPhamKhachHangResponse;
import com.shop.server.entities.main.VoucherSanPhamKhachHang;
import com.shop.server.repositories.VoucherSanPhamKhachHangRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.util.List;

@Repository
public interface AdminVoucherSanPhamKhachHangRepository extends VoucherSanPhamKhachHangRepository {
    @Query(value = """
                    SELECT  vc.id as id,
                            vc.id_khach_hang as idKhachHang,
                            vc.id_san_pham as idSanPhamChiTiet,
                            vc.id_voucher as idVoucher,
                            vc.so_luong as soLuong
                    FROM voucher_san_pham_khach_hang vc
                    WHERE vc.deleted = 0
                    ORDER BY vc.ngay_tao DESC
""",nativeQuery = true)
    List<AdminVoucherSanPhamKhachHangResponse> getList(Pageable pageable);

    @Query(value = """
        INSERT INTO voucher_san_pham_khach_hang (id_san_pham, id_khach_hang, id_phieu_giam_gia, so_luong)
                SELECT
                    sp.id_san_pham, kh.id_khach_hang, :#{request.idVoucher},:#{request.soLuong}
                FROM
                    UNNEST(:#{#request.idSanPham}) AS sp(id_san_pham),
                    UNNEST(:#{request.idKhachHang}) AS kh(id_khach_hang)
""",nativeQuery = true)
    void saveVoucherSanPhamKhachHang(VoucherSanPhamKhachHangRequest request);
}
