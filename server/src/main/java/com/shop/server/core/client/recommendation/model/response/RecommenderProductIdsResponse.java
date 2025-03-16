package com.shop.server.core.client.recommendation.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RecommenderProductIdsResponse {

    private List<String> recommendedProductIds;

}
