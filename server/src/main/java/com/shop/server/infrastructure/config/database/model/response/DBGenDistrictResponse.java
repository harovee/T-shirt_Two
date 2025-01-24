package com.shop.server.infrastructure.config.database.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DBGenDistrictResponse {

    @JsonProperty("DistrictID")
    private Long districtID;

    @JsonProperty("DistrictName")
    private String districtName;

    @JsonProperty("ProvinceID")
    private Long provinceID;

}
