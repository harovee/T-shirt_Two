package com.shop.server.core.admin.chat_lieu.service;

import com.shop.server.core.admin.chat_lieu.model.request.AdCreateUpdateChatLieuRequest;
import com.shop.server.core.admin.chat_lieu.model.request.AdFindChatLieuRequest;
import com.shop.server.core.admin.product.models.requests.AdminFindProductRequest;
import com.shop.server.core.admin.product.models.requests.AdminProductRequest;
import com.shop.server.core.common.base.ResponseObject;
import jakarta.validation.Valid;

public interface AdChatLieuService {

    ResponseObject<?> getAllChatLieus(AdFindChatLieuRequest request);

    ResponseObject<?> getChatLieu(String id);

    ResponseObject<?> createChatLieu(@Valid AdCreateUpdateChatLieuRequest request);

    ResponseObject<?> updateChatLieu(String id, @Valid AdCreateUpdateChatLieuRequest request);

    ResponseObject<?> deleted(String id);

    ResponseObject<?> getListChatLieu();

}
