package com.shop.server.core.client.momo.services;

import com.shop.server.core.client.momo.model.MomoResponse;
import com.shop.server.core.client.momo.model.request.ClientMomoRequest;
import org.springframework.stereotype.Service;

@Service
public interface IMomoService {
    MomoResponse createMomo(ClientMomoRequest request);
}
