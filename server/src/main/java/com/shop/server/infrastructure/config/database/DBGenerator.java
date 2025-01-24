package com.shop.server.infrastructure.config.database;

import com.shop.server.infrastructure.config.database.service.DBGenEntityService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DBGenerator {

    @Value("${db.generator.is-generated}")
    private String isGenerated;

    @Autowired
    public DBGenerator(DBGenEntityService service) {
        this.service = service;
    }

    @PostConstruct
    public void init() {
        if ("true".equals(isGenerated)) generateData();
    }

    private DBGenEntityService service;

    private void generateData() {
        service.synchronizationProvince();
    }

}
