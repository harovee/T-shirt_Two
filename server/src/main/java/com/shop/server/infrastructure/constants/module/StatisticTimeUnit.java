package com.shop.server.infrastructure.constants.module;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum StatisticTimeUnit {

    YEAR,
    MONTH,
    WEEK,
    DAY;

    public static List<String> getStatisticTimeUnits() {
        return Arrays.stream(StatisticTimeUnit.values())
                .map(Enum::name)
                .collect(Collectors.toList());
    }

}
