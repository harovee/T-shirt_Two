package com.shop.server.core.client.recommendation.service.impl;

import com.shop.server.core.client.recommendation.model.response.RecommenderOrderHistoryResponse;
import com.shop.server.core.client.recommendation.model.response.RecommenderProductResponse;
import com.shop.server.core.client.recommendation.model.response.RecommenderUserResponse;
import com.shop.server.core.client.recommendation.repository.RecommenderSysRepository;
import com.shop.server.core.client.recommendation.service.RecommenderDataForHybridService;
import com.shop.server.core.client.recommendation.service.RecommenderSysService;
import com.shop.server.core.common.base.ResponseObject;
import com.shop.server.infrastructure.constants.module.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RecommenderSysServiceImpl implements RecommenderSysService {

    private final RecommenderDataForHybridService recommenderDataForHybridService;

    private final RecommenderSysRepository recommenderSysRepository;

    @Override
    public ResponseObject<?> getRecommenderSysList() {




        return new ResponseObject<>(
                null,
                HttpStatus.OK,
                Message.Success.GET_SUCCESS
        );
    }

    @Override
    public ResponseObject<?> getDataForHybrid() {
        Map<String, Object> data = new HashMap<>();

        List<RecommenderOrderHistoryResponse> orderHistories = recommenderDataForHybridService.getOrderHistoriesForHybrid();
        List<RecommenderUserResponse> users = recommenderDataForHybridService.getUsersForHybrid();
        List<RecommenderProductResponse> products = recommenderDataForHybridService.getProductsForHybrid();

        data.put("orderHistories", orderHistories);
        data.put("users", users);
        data.put("products", products);

        return new ResponseObject<>(data, HttpStatus.OK, Message.Success.GET_SUCCESS);
    }

}
