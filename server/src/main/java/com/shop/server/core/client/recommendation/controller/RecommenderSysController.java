package com.shop.server.core.client.recommendation.controller;

import com.shop.server.core.client.recommendation.service.RecommenderSysService;
import com.shop.server.infrastructure.constants.module.MappingConstant;
import com.shop.server.utils.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ** Data request
 * 1. order history
 * 2. all product list on sys
 * 3. user profile
 * ** Algorithm applied: combine 2 methods
 *
 * @ method 1:
 * Using Collaborative Filtering ( Dựa vào order history của các user same )
 * Using ALS (Alternating Least Squares) from Surprise Library
 * @ method 2:
 * Deep Learning (Neural Network)
 * (Dự đoán sở thích khách hàng dựa vào metadata sản phẩm và hồ sơ khách hàng)
 * {age, gender, address, ...}
 */
@RestController
@RequestMapping(MappingConstant.API_CLIENT_RECOMMENDER)
@RequiredArgsConstructor
public class RecommenderSysController {

    private final RecommenderSysService recommenderSysService;

    @GetMapping
    public ResponseEntity<?> getRecommendation() {
        return Helper.createResponseEntity(recommenderSysService.getRecommenderSysList());
    }

    @GetMapping("/data")
    public ResponseEntity<?> getDataForHybrid() {
        return Helper.createResponseEntity(recommenderSysService.getDataForHybrid());
    }

}
