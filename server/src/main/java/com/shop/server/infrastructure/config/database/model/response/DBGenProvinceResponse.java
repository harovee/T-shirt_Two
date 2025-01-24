package com.shop.server.infrastructure.config.database.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
public class DBGenProvinceResponse {

    @JsonProperty("ProvinceID")
    private Long provinceID;

    @JsonProperty("ProvinceName")
    private String provinceName;

    @JsonProperty("Code")
    private String code;

    @JsonProperty("IsEnable")
    private Long isEnable;

}