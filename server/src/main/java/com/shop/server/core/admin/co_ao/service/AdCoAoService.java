package com.shop.server.core.admin.co_ao.service;

import com.shop.server.core.admin.chat_lieu.model.request.AdCreateUpdateChatLieuRequest;
import com.shop.server.core.admin.chat_lieu.model.request.AdFindChatLieuRequest;
import com.shop.server.core.admin.co_ao.model.request.AdCreateUpdateCoAoRequest;
import com.shop.server.core.common.base.ResponseObject;
import jakarta.validation.Valid;

public interface AdCoAoService {

    ResponseObject<?> getListCoAo();

    ResponseObject<?> createCoAo(@Valid AdCreateUpdateCoAoRequest request);

}
