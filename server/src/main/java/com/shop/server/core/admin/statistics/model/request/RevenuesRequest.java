package com.shop.server.core.admin.statistics.model.request;

import com.shop.server.infrastructure.constants.module.StatisticTimeUnit;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class RevenuesRequest {

    private String statisticType;

    private String timeUnit;

    private Long startTime;

    private Long endTime;

}
