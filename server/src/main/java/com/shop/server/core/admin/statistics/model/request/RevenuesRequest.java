package com.shop.server.core.admin.statistics.model.request;

import com.shop.server.core.common.base.PageableRequest;
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
public class RevenuesRequest extends PageableRequest {

    private String statisticType;

    private String timeUnit;

    private Long startTime;

    private Long endTime;

}
