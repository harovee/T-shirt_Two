package com.shop.server.core.client.recommendation.service;

import com.shop.server.core.common.base.ResponseObject;

public interface RecommenderSysService {

    ResponseObject<?> getRecommenderSysList();

    ResponseObject<?> getDataForHybrid();

}
