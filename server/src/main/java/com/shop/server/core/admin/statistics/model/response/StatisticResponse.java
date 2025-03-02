package com.shop.server.core.admin.statistics.model.response;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StatisticResponse {

    private List<RevenueResponse> revenues;

    private NumberOrderByStatusResponse numberOrderByStatus;

    private Long numberNewCustomers;
}
