package com.shop.server.core.client.recommendation.model.response;

public interface RecommenderOrderHistoryResponse {

    String getClientId();

    String getProductVariantId();

    Long getPurchaseTimestamp();

    Long getQuantity();

    Double getPrice();

}
