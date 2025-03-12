package com.shop.server.core.client.recommendation.service;

import java.util.List;

public interface RecommenderWebClientService {

    List<String> callApiGetProductIdsRecommendation(String userId);

}
