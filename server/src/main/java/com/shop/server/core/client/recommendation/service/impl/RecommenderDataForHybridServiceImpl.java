package com.shop.server.core.client.recommendation.service.impl;

import com.shop.server.core.client.recommendation.model.response.RecommenderOrderHistoryResponse;
import com.shop.server.core.client.recommendation.model.response.RecommenderProductResponse;
import com.shop.server.core.client.recommendation.model.response.RecommenderUserResponse;
import com.shop.server.core.client.recommendation.repository.RecommenderSysRepository;
import com.shop.server.core.client.recommendation.service.RecommenderDataForHybridService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommenderDataForHybridServiceImpl implements RecommenderDataForHybridService {

    private final RecommenderSysRepository recommenderSysRepository;

    @Override
    public List<RecommenderOrderHistoryResponse> getOrderHistoriesForHybrid() {
        return recommenderSysRepository.getOrderHistories();
    }

    @Override
    public List<RecommenderUserResponse> getUsersForHybrid() {
        return recommenderSysRepository.getUsers();
    }

    @Override
    public List<RecommenderProductResponse> getProductsForHybrid() {
        return recommenderSysRepository.getProducts();
    }

}
