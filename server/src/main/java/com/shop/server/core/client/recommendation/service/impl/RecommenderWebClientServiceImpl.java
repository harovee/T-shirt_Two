package com.shop.server.core.client.recommendation.service.impl;

import com.shop.server.core.client.recommendation.model.response.RecommenderProductIdsResponse;
import com.shop.server.core.client.recommendation.service.RecommenderWebClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class RecommenderWebClientServiceImpl implements RecommenderWebClientService {

    @Value("${python.url}")
    private String api;

    private final WebClient webClient;

    public RecommenderWebClientServiceImpl(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Override
    public List<String> callApiGetProductIdsRecommendation(String userId) {
        String url = api + "recommendations/" + userId;
        try {
            return webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(RecommenderProductIdsResponse.class)
                    .map(RecommenderProductIdsResponse::getRecommendedProductIds)
                    .defaultIfEmpty(Collections.emptyList())
                    .block();
        } catch (WebClientResponseException e) {
            log.error("API error: {} - {}", e.getRawStatusCode(), e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            log.error("Unexpected error when calling API: {}", e.getMessage(), e);
        }
        return Collections.emptyList();
    }
}
