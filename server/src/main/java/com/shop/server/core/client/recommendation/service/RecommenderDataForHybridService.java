package com.shop.server.core.client.recommendation.service;

import com.shop.server.core.client.recommendation.model.response.RecommenderOrderHistoryResponse;
import com.shop.server.core.client.recommendation.model.response.RecommenderProductResponse;
import com.shop.server.core.client.recommendation.model.response.RecommenderUserResponse;
import com.shop.server.core.common.base.ResponseObject;

import java.util.List;

public interface RecommenderDataForHybridService {

    List<RecommenderOrderHistoryResponse> getOrderHistoriesForHybrid();

    List<RecommenderUserResponse> getUsersForHybrid();

    List<RecommenderProductResponse> getProductsForHybrid();

}
