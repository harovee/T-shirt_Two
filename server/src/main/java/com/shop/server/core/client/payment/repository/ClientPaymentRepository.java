package com.shop.server.core.client.payment.repository;

import com.shop.server.core.admin.point_of_sale.model.request.AdPOSUpdateCartRequest;
import com.shop.server.core.client.payment.model.request.ClientInvoiceDetailRequest;
import com.shop.server.core.client.payment.model.request.ClientPaymentRequest;
import com.shop.server.repositories.HoaDonChiTietRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface ClientPaymentRepository extends HoaDonChiTietRepository {

    @Modifying
    @Transactional
    @Query(value = """
    -- Chỉ thêm vào hóa đơn chi tiết nếu số lượng còn đủ
    INSERT INTO hoa_don_chi_tiet
    (id, ngay_tao, deleted, ngay_sua, nguoi_sua, nguoi_tao,
     gia, so_luong, thanh_tien,
     trang_thai, id_hoa_don, id_san_pham_chi_tiet)
    SELECT UUID(),
           UNIX_TIMESTAMP()*1000,
           false,
           UNIX_TIMESTAMP()*1000,
           :#{#req.userEmail},
           :#{#req.userEmail},
           :#{#req.gia},
           :#{#req.soLuong},
           :#{#req.gia} * :#{#req.soLuong},
           'PENDING',
           :idHoaDon,
           spct.id
    FROM san_pham_chi_tiet spct
    WHERE spct.id = :#{#req.idSanPhamChiTiet}
          AND spct.so_luong >= :#{#req.soLuong};

    UPDATE san_pham_chi_tiet spct
    SET spct.so_luong = spct.so_luong - :#{#req.soLuong}
    WHERE spct.id = :#{#req.idSanPhamChiTiet}
          AND spct.so_luong >= :#{#req.soLuong};
""", nativeQuery = true)
    void saveProductDetailsToInvoice(ClientInvoiceDetailRequest req, String idHoaDon);
}
