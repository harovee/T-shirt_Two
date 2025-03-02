package com.shop.server.core.admin.statistics.repository;

import com.shop.server.core.admin.statistics.model.request.RevenuesRequest;
import com.shop.server.core.admin.statistics.model.response.NumberOrderByStatusResponse;
import com.shop.server.core.admin.statistics.model.response.RevenueResponse;
import com.shop.server.core.admin.statistics.model.response.StatisticProductResponse;
import com.shop.server.repositories.HoaDonRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StatisticRepository extends HoaDonRepository {

    @Query(value = """
                SELECT
                    COUNT(CASE WHEN o.trang_thai = 'Thành công' THEN 1 END) AS number_success_order,
                    COUNT(CASE WHEN o.trang_thai = 'Đã thanh toán' THEN 1 END) AS number_payment_order,
                    COUNT(CASE WHEN o.trang_thai = 'Đã giao hàng' THEN 1 END) AS number_delivery_order,
                    COUNT(CASE WHEN o.trang_thai = 'Đang vận chuyển' THEN 1 END) AS number_shipping_order,
                    COUNT(CASE WHEN o.trang_thai = 'Chờ giao hàng' THEN 1 END) AS number_delivery_waiting_order,
                    COUNT(CASE WHEN o.trang_thai = 'Đã hủy' THEN 1 END) AS number_cancel_order,
                    COUNT(CASE WHEN o.trang_thai = 'Chờ xác nhận' THEN 1 END) AS number_confirm_waiting_order,
                    COUNT(CASE WHEN o.trang_thai = 'Hóa đơn chờ' THEN 1 END) AS number_waiting_order
                FROM hoa_don o
                WHERE (o.deleted is null or o.deleted != 1)
                AND ( o.ngay_tao BETWEEN :#{#req.startTime} AND :#{#req.endTime} )
            """, nativeQuery = true)
    NumberOrderByStatusResponse getOrderStatisticsByStatus(RevenuesRequest req);


    @Query(value = """
            SELECT COUNT(kh.id) FROM khach_hang kh
            WHERE kh.deleted != 1 AND kh.ngay_tao BETWEEN :#{#req.startTime} AND :#{#req.endTime}
            """, nativeQuery = true)
    Long getNumberNewCustomer(RevenuesRequest req);

    @Query(value = """
                select ROW_NUMBER() OVER(ORDER BY sum(spct.so_luong)) AS catalog,
                 sp.id, sp.ma_san_pham, sp.ten as ten_san_pham,
                 dm.ten as ten_danh_muc, sum(spct.so_luong) as so_luong
                from san_pham sp
                join danh_muc dm on dm.id = sp.id_danh_muc
                left join san_pham_chi_tiet spct on sp.id = spct.id_san_pham
                where (sp.deleted is null or sp.deleted != 1) and so_luong <= 10
                and (?1 is null
                or (sp.ten LIKE CONCAT('%', ?1, '%'))
                or (sp.ma_san_pham LIKE CONCAT('%', ?1, '%')))
                group by sp.id, sp.ma_san_pham, sp.ten, dm.ten
            """, nativeQuery = true)
    Page<StatisticProductResponse> getProductsOutStock(String key, Pageable pageable);


    @Query(value = """
                WITH revenue_data AS (
                     SELECT
                         hd.id AS id_hoa_don,
                         get_time_display( :#{#req.timeUnit}, hd.ngay_tao ) AS time_display,
                         hd.tong_tien AS total_revenue,
                         (SELECT SUM(hdct.so_luong) FROM hoa_don_chi_tiet hdct WHERE hdct.id_hoa_don = hd.id) AS number_product_sold
                     FROM hoa_don hd
                     WHERE (hd.deleted IS NULL OR hd.deleted != 1)
                       AND hd.trang_thai = 'Thành công'
                       AND hd.ngay_tao BETWEEN :#{#req.startTime} AND :#{#req.endTime}
                 )
                 SELECT
                     time_display AS timeDisplay,
                     '' AS objectValue,
                     sum(total_revenue) AS totalRevenue,
                     count(id_hoa_don) AS numberOrder,
                     sum(number_product_sold) AS numberProductSold
                 FROM revenue_data
                 group by timeDisplay, objectValue
            """, nativeQuery = true)
    List<RevenueResponse> getRevenueStatistics(RevenuesRequest req);

    @Query(value = """
                WITH revenue_data AS (
                     SELECT
                         hd.id AS id_hoa_don,
                         get_time_display( :#{#req.timeUnit}, hd.ngay_tao ) AS time_display,
                         hd.tong_tien AS total_revenue,
                         (SELECT SUM(hdct.so_luong) FROM hoa_don_chi_tiet hdct WHERE hdct.id_hoa_don = hd.id) AS number_product_sold
                     FROM hoa_don hd
                     WHERE (hd.deleted IS NULL OR hd.deleted != 1)
                       AND hd.trang_thai = 'Thành công'
                       AND hd.ngay_tao BETWEEN :#{#req.startTime} AND :#{#req.endTime}
                 )
                 SELECT
                     time_display AS timeDisplay,
                     '' AS objectValue,
                     sum(total_revenue) AS totalRevenue,
                     count(id_hoa_don) AS numberOrder,
                     sum(number_product_sold) AS numberProductSold
                 FROM revenue_data
                 group by timeDisplay, objectValue
            """, nativeQuery = true)
    Page<RevenueResponse> getRevenueStatistics(Pageable pageable, RevenuesRequest req);


    @Query(value = """
                SELECT
                   CONCAT(dm.ma_danh_muc, ' - ', dm.ten) AS object_value,
                   COALESCE(SUM(hd.tong_tien), 0) AS total_revenue,
                   COALESCE(COUNT(DISTINCT hd.id), 0) AS number_order,
                   COALESCE(SUM(hdct.so_luong), 0) AS number_product_sold
               FROM danh_muc dm
               LEFT JOIN san_pham sp ON dm.id = sp.id_danh_muc
               LEFT JOIN san_pham_chi_tiet spct ON sp.id = spct.id_san_pham
               LEFT JOIN hoa_don_chi_tiet hdct ON spct.id = hdct.id_san_pham_chi_tiet
               LEFT JOIN hoa_don hd ON hdct.id_hoa_don = hd.id
                   AND (hd.deleted is null or hd.deleted != 1)
                   AND hd.trang_thai = 'Thành công'
                   AND hd.ngay_tao BETWEEN :#{#req.startTime} AND :#{#req.endTime}
               GROUP BY dm.ma_danh_muc, dm.ten
               ORDER BY total_revenue DESC
            """, nativeQuery = true)
    List<RevenueResponse> getRevenuesByCategory(RevenuesRequest req);

    @Query(value = """
                SELECT
                     CONCAT(nv.ma_nhan_vien, ' - ', nv.ho_va_ten) AS object_value,
                     COALESCE(SUM(hd.tong_tien), 0) AS total_revenue,
                     COALESCE(COUNT(hd.id), 0) AS number_order,
                     COALESCE(SUM(hdct.so_luong), 0) AS number_product_sold
                FROM nhan_vien nv
                LEFT JOIN hoa_don hd ON hd.id_nhan_vien = nv.id
                    AND (hd.deleted is null or hd.deleted != 1)
                    AND hd.trang_thai = 'Thành công'
                    AND hd.ngay_tao BETWEEN :#{#req.startTime} AND :#{#req.endTime}
                LEFT JOIN hoa_don_chi_tiet hdct ON hd.id = hdct.id_hoa_don
                GROUP BY nv.ma_nhan_vien, nv.ho_va_ten
                ORDER BY total_revenue DESC
            """, nativeQuery = true)
    List<RevenueResponse> getRevenuesByEmployee(RevenuesRequest req);

    @Query(value = """
                SELECT
                     hd.loai_hoa_don AS object_value,
                     COALESCE(SUM(hd.tong_tien), 0) AS total_revenue,
                     COALESCE(COUNT(hd.id), 0) AS number_order,
                     COALESCE(SUM(hdct.so_luong), 0) AS number_product_sold
                FROM hoa_don hd
                JOIN hoa_don_chi_tiet hdct ON hd.id = hdct.id_hoa_don
                WHERE (hd.deleted is null or hd.deleted != 1)
                    AND hd.trang_thai = 'Thành công'
                    AND hd.ngay_tao BETWEEN :#{#req.startTime} AND :#{#req.endTime}
                GROUP BY hd.loai_hoa_don
                ORDER BY total_revenue DESC
            """, nativeQuery = true)
    List<RevenueResponse> getRevenuesByOrderType(RevenuesRequest req);


    @Query(value = """
            SELECT
                CONCAT(sp.ma_san_pham, ' - ', sp.ten)  AS object_value,
                COALESCE(SUM(hdct.thanh_tien), 0) AS total_revenue,
                COALESCE(COUNT(hd.id), 0) AS number_order,
                COALESCE(SUM(hdct.so_luong), 0) AS number_product_sold
            FROM hoa_don hd
            JOIN hoa_don_chi_tiet hdct ON hd.id = hdct.id_hoa_don
            JOIN san_pham_chi_tiet spct ON hdct.id_san_pham_chi_tiet = spct.id
            JOIN san_pham sp ON spct.id_san_pham = sp.id
            WHERE (hd.deleted is null or hd.deleted != 1) AND hd.trang_thai = 'Thành công'
            AND hd.ngay_tao BETWEEN :#{#req.startTime} AND :#{#req.endTime}
            GROUP BY sp.ma_san_pham, sp.ten
            ORDER BY number_product_sold DESC
            LIMIT 5;
            """, nativeQuery = true)
    List<RevenueResponse> getTop10ProductBestSaleByRangeTime(RevenuesRequest req);

    @Query(value = """
            SELECT
                CONCAT(sp.ma_san_pham, ' - ', sp.ten)  AS object_value,
                COALESCE(SUM(hdct.thanh_tien), 0) AS total_revenue,
                COALESCE(COUNT(hd.id), 0) AS number_order,
                COALESCE(SUM(hdct.so_luong), 0) AS number_product_sold
            FROM hoa_don hd
            JOIN hoa_don_chi_tiet hdct ON hd.id = hdct.id_hoa_don
            JOIN san_pham_chi_tiet spct ON hdct.id_san_pham_chi_tiet = spct.id
            JOIN san_pham sp ON spct.id_san_pham = sp.id
            WHERE (hd.deleted is null or hd.deleted != 1) AND hd.trang_thai = 'Thành công'
            GROUP BY sp.ma_san_pham, sp.ten
            ORDER BY number_product_sold DESC
            LIMIT 5;
            """, nativeQuery = true)
    List<RevenueResponse> getTop10ProductBestSale();

}
