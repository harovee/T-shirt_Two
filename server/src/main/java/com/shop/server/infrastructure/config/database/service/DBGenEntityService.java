package com.shop.server.infrastructure.config.database.service;

public interface DBGenEntityService {

    void synchronizationProvince();

    void synchronizationDistrict(Long id);

    void synchronizationWard(Long id);

}
