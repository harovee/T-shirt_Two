package com.shop.server.core.admin.statistics.service.iml;

import com.shop.server.core.admin.statistics.model.request.RevenuesRequest;
import com.shop.server.core.admin.statistics.repository.StatisticRepository;
import com.shop.server.core.admin.statistics.service.StatisticService;
import com.shop.server.core.common.base.PageableObject;
import com.shop.server.core.common.base.ResponseObject;
import com.shop.server.infrastructure.constants.module.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class StatisticServiceIml implements StatisticService {

    private final StatisticRepository statisticRepository;

    public StatisticServiceIml(StatisticRepository statisticRepository) {
        this.statisticRepository = statisticRepository;
    }


    @Override
    public ResponseObject<?> getStatisticData(RevenuesRequest request) {
        String type = request.getStatisticType();
        type = type == null ? "" : type;
        Map<String, Object> statisticData = new HashMap<>();
        switch (type) {
            case "category":
                statisticData.put("revenuesType", getRevenuesByCategory(request));
                break;
            case "employee":
                statisticData.put("revenuesType", getRevenuesByEmployee(request));
                break;
            case "order_type":
                statisticData.put("revenuesType", getRevenuesByOrderType(request));
                break;
            default:
                statisticData.put("revenuesType", null);

        }
        statisticData.put("revenues", getRevenues(request));
        statisticData.put("numberOrderByStatus", getOrderQuantityByStatus(request));
        statisticData.put("numberNewCustomers", getNumberNewCustomers(request));
        statisticData.put("top10ProductBestSaleByRangeTime", getTop10BestSaleByRangeTime(request));

        return ResponseObject.successForward(statisticData, Message.Success.GET_SUCCESS);
    }

    @Override
    public ResponseObject<?> getRevenues(RevenuesRequest request) {
        return ResponseObject.successForward(
                statisticRepository.getRevenueStatistics(request),
                Message.Success.GET_SUCCESS
        );
    }

    @Override
    public ResponseObject<?> getRevenuesPage(RevenuesRequest request) {
        Pageable pageable = PageRequest.of((request.getPage()-1), 5);
        return new ResponseObject<>(
                PageableObject.of(statisticRepository.getRevenueStatistics(pageable, request)),
                HttpStatus.OK,
                Message.Success.GET_SUCCESS
        );
    }

    @Override
    public ResponseObject<?> getRevenuesByCategory(RevenuesRequest request) {
        return ResponseObject.successForward(
                statisticRepository.getRevenuesByCategory(request),
                Message.Success.GET_SUCCESS
        );
    }

    @Override
    public ResponseObject<?> getRevenuesByEmployee(RevenuesRequest request) {
        return ResponseObject.successForward(
                statisticRepository.getRevenuesByEmployee(request),
                Message.Success.GET_SUCCESS
        );
    }

    @Override
    public ResponseObject<?> getRevenuesByOrderType(RevenuesRequest request) {
        return ResponseObject.successForward(
                statisticRepository.getRevenuesByOrderType(request),
                Message.Success.GET_SUCCESS
        );
    }

    @Override
    public ResponseObject<?> getOrderQuantityByStatus(RevenuesRequest request) {
        return ResponseObject.successForward(
                statisticRepository.getOrderStatisticsByStatus(request),
                Message.Success.GET_SUCCESS
        );
    }

    @Override
    public ResponseObject<?> getTop10BestSale() {
        return ResponseObject.successForward(
                statisticRepository.getTop10ProductBestSale(),
                Message.Success.GET_SUCCESS
        );
    }

    @Override
    public ResponseObject<?> getTop10BestSaleByRangeTime(RevenuesRequest request) {
        return ResponseObject.successForward(
                statisticRepository.getTop10ProductBestSaleByRangeTime(request),
                Message.Success.GET_SUCCESS
        );
    }

    @Override
    public ResponseObject<?> getProductsOutStock(String key, int currentPage) {
        Pageable pageable = PageRequest.of((currentPage - 1), 5);
        return new ResponseObject<>(
                PageableObject.of(statisticRepository.getProductsOutStock(key, pageable)),
                HttpStatus.OK,
                Message.Success.GET_SUCCESS
        );
    }

    @Override
    public Long getNumberNewCustomers(RevenuesRequest request) {
        return statisticRepository.getNumberNewCustomer(request);
    }
}
