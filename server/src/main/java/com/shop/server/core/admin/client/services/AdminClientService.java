package com.shop.server.core.admin.client.services;

import com.shop.server.core.admin.client.models.requests.ClientFindProductRequest;
import com.shop.server.core.admin.client.models.requests.ClientProductRequest;
import com.shop.server.core.common.base.ResponseObject;
import jakarta.validation.Valid;

public interface AdminClientService {

    ResponseObject<?> getClients(ClientFindProductRequest request);

    ResponseObject<?> getClientById(String id);

    ResponseObject<?> createClient(@Valid ClientProductRequest request);

    ResponseObject<?> updateClient(String id, @Valid ClientProductRequest request);

    ResponseObject<?> changeStatusClient(String id);


}
