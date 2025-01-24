package com.shop.server.infrastructure.config.database.service.impl;

import com.shop.server.core.common.base.ResponseObjectGHTK;
import com.shop.server.infrastructure.config.database.model.response.DBGenDistrictResponse;
import com.shop.server.infrastructure.config.database.model.response.DBGenProvinceResponse;
import com.shop.server.infrastructure.config.database.model.response.DBGenWardResponse;
import com.shop.server.infrastructure.config.database.service.DBGenWebClientService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class DBGenWebClientServiceImpl implements DBGenWebClientService {

    @Value("${api.ghtk.token}")
    private String token;

    @Value("${api.ghtk}")
    private String api;

    @Override
    public List<DBGenProvinceResponse> callApiGetProvince() {
        try {
            String url = api + "/province?country_id=1";
            WebClient webClient = WebClient.create(url);
            Mono<ResponseObjectGHTK<List<DBGenProvinceResponse>>> responseMono = webClient
                    .get()
                    .header("token", token)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ResponseObjectGHTK<List<DBGenProvinceResponse>>>() {
                    });

            if (responseMono.block() != null) {
                ResponseObjectGHTK<List<DBGenProvinceResponse>> responseObject = responseMono.block();
                List<DBGenProvinceResponse> responses = Objects.requireNonNull(responseObject).getData();
                log.info("DBGenProvinceResponse data: {}", responses.size());
                return responses;
            } else {
                log.info("Error when calling api get list province: data null");
            }
        } catch (Exception e) {
            log.error("Error when calling api get list province for callApiGetProvince: {}", e.getMessage());
        }
        return null;
    }

    @Override
    public List<DBGenDistrictResponse> callApiGetDistrict(Long id) {
        try {
            String url = api + "/district?province_id=" + id;
            WebClient webClient = WebClient.create(url);
            Mono<ResponseObjectGHTK<List<DBGenDistrictResponse>>> responseMono = webClient
                    .get()
                    .header("token", token)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ResponseObjectGHTK<List<DBGenDistrictResponse>>>() {
                    });

            if (responseMono.block() != null) {
                ResponseObjectGHTK<List<DBGenDistrictResponse>> responseObject = responseMono.block();
                List<DBGenDistrictResponse> responses = Objects.requireNonNull(responseObject).getData();
                log.info("DBGenDistrictResponse data: {}", responses.size());
                return responses;
            } else {
                log.info("Error when calling api get list district: data null");
            }
        } catch (Exception e) {
            log.error("Error when calling api get list district for callApiGetDistrict: {}", e.getMessage());
        }
        return null;
    }

    @Override
    public List<DBGenWardResponse> callApiGetWard(Long id) {
        try {
            String url = api + "/ward?district_id=" + id;
            WebClient webClient = WebClient.create(url);
            Mono<ResponseObjectGHTK<List<DBGenWardResponse>>> responseMono = webClient
                    .get()
                    .header("token", token)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ResponseObjectGHTK<List<DBGenWardResponse>>>() {
                    });

            if (responseMono.block() != null) {
                ResponseObjectGHTK<List<DBGenWardResponse>> responseObject = responseMono.block();
                List<DBGenWardResponse> responses = Objects.requireNonNull(responseObject).getData();
                log.info("DBGenWardResponse data: {}", responses.size());
                return responses;
            } else {
                log.info("Error when calling api get list ward: data null");
            }
        } catch (Exception e) {
            log.error("Error when calling api get list ward for callApiGetWard: {}", e.getMessage());
        }
        return null;
    }
}
