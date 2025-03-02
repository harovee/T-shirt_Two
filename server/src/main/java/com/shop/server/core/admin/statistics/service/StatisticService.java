package com.shop.server.core.admin.statistics.service;

import com.shop.server.core.admin.statistics.model.request.RevenuesRequest;
import com.shop.server.core.common.base.ResponseObject;

public interface StatisticService {

    /* -- Các danh sách có thể lọc theo thời gian tự chọn
     * [] Danh sách tổng doanh thu (kèn số lượng đơn và số lượng sản phẩm bán ra)
     * [] Doanh thu theo danh mục
     * [] Doanh thu theo nhân viên bán hàng
     * [] Doanh thu theo loại hóa đơn
     *
     * [] Các số lượng đơn hàng theo trạng thái:
     *
     * Khác:
     * [] Top 10 danh sách sản phẩm bán chạy nhất
     * [] Danh sách sản phẩm sắp hết hàng (<= 5)
     * [] Danh sách khách hàng mới (2 loại)
     *
     *  */

    ResponseObject<?> getStatisticData(RevenuesRequest request);

    ResponseObject<?> getRevenues(RevenuesRequest request);

    ResponseObject<?> getRevenuesPage(RevenuesRequest request, int currentPage);

    ResponseObject<?> getRevenuesByCategory(RevenuesRequest request);

    ResponseObject<?> getRevenuesByEmployee(RevenuesRequest request);

    ResponseObject<?> getRevenuesByOrderType(RevenuesRequest request);

    ResponseObject<?> getOrderQuantityByStatus(RevenuesRequest request);

    ResponseObject<?> getTop10BestSale();

    ResponseObject<?> getTop10BestSaleByRangeTime(RevenuesRequest request);

    ResponseObject<?> getProductsOutStock(String key, int currentPage);

    Long getNumberNewCustomers(RevenuesRequest request);

}
