package com.shop.server.core.admin.statistics.model.response;


import java.math.BigDecimal;

public interface RevenueResponse {

    String getTimeDisplay();

    String getObjectValue();

    BigDecimal getTotalRevenue();

    Long getNumberOrder();

    Long getNumberProductSold();


}
