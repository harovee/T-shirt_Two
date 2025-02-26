package com.shop.server.core.admin.statistics.controller;

import com.shop.server.core.admin.statistics.model.request.RevenuesRequest;
import com.shop.server.core.admin.statistics.service.iml.StatisticServiceIml;
import com.shop.server.infrastructure.constants.module.MappingConstant;
import com.shop.server.infrastructure.constants.module.StatisticTimeUnit;
import com.shop.server.utils.Helper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.ZoneId;

@RequestMapping(MappingConstant.API_ADMIN_STATISTIC)
@RestController
@CrossOrigin("*")
public class StatisticController {

    private final StatisticServiceIml statisticService;

    public StatisticController(StatisticServiceIml statisticServiceIml) {
        this.statisticService = statisticServiceIml;
    }

    @GetMapping("/statistic-data")
    public ResponseEntity<?> getStatisticData(final RevenuesRequest request) {
        return Helper.createResponseEntity(statisticService.getStatisticData(request));
    }

    @GetMapping("/statistic-data/page")
    public ResponseEntity<?> getRevenuePage(final RevenuesRequest request, @RequestParam int currentPage) {
        return Helper.createResponseEntity(statisticService.getRevenuesPage(request, currentPage));
    }

    @GetMapping("/top-10-products-best-sale")
    public ResponseEntity<?> getTop10ProductsBestSale() {
        return Helper.createResponseEntity(statisticService.getTop10BestSale());
    }

    @GetMapping("/out-stock-products")
    public ResponseEntity<?> getOutStockProducts(@RequestParam String key,
                                                 @RequestParam int currentPage) {
        return Helper.createResponseEntity(statisticService.getProductsOutStock(key, currentPage));
    }

    @GetMapping("/today")
    public ResponseEntity<?> getToDayStatistic() {
        RevenuesRequest request = new RevenuesRequest();
        request.setTimeUnit(StatisticTimeUnit.DAY);
        request.setStartTime(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());
        request.setEndTime(System.currentTimeMillis());

        // trả ra list revenue chỉ 1 bản ghi cho ngày hiện tại
        return Helper.createResponseEntity(statisticService.getStatisticData(request));
    }



}