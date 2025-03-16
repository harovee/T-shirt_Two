package com.shop.server.core.client.momo.client;

import com.shop.server.core.client.momo.model.MomoRequest;
import com.shop.server.core.client.momo.model.MomoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "momoApi" , url = "${momo.end_point}")
public interface MomoApi {

    @PostMapping("/create")
    MomoResponse createMomo(@RequestBody MomoRequest request);
}
