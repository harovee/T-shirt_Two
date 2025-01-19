package com.shop.server.core.admin.client.services;

import com.shop.server.core.admin.client.models.requests.AdminClientRequest;
import com.shop.server.core.admin.client.models.requests.AdminFindClientRequest;
import com.shop.server.core.common.base.ResponseObject;
import jakarta.validation.Valid;

public interface AdminClientService {

    ResponseObject<?> getClients(AdminFindClientRequest request);

    ResponseObject<?> getClientById(String id);

    ResponseObject<?> createClient(@Valid AdminClientRequest request);

    ResponseObject<?> updateClient(String id, @Valid AdminClientRequest request);

    ResponseObject<?> changeStatusClient(String id);

    ResponseObject<?> updateClientAvatar(String id, AdminClientRequest request);

}
