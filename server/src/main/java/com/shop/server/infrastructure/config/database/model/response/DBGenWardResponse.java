package com.shop.server.infrastructure.config.database.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DBGenWardResponse {

    @JsonProperty("WardName")
    private String wardName;

    @JsonProperty("WardCode")
    private String code;

    @JsonProperty("DistrictID")
    private Long districtID;

}
