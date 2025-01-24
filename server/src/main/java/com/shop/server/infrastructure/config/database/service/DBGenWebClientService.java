package com.shop.server.infrastructure.config.database.service;

import com.shop.server.infrastructure.config.database.model.response.DBGenDistrictResponse;
import com.shop.server.infrastructure.config.database.model.response.DBGenProvinceResponse;
import com.shop.server.infrastructure.config.database.model.response.DBGenWardResponse;

import java.util.List;

public interface DBGenWebClientService {

    List<DBGenProvinceResponse> callApiGetProvince();

    List<DBGenDistrictResponse> callApiGetDistrict(Long id);

    List<DBGenWardResponse> callApiGetWard(Long id);

}
