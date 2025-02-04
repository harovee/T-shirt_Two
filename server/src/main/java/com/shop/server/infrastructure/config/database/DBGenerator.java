package com.shop.server.infrastructure.config.database;

import com.shop.server.infrastructure.config.database.service.DBGenEntityService;
import com.shop.server.infrastructure.config.database.service.DBGenStaffService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DBGenerator {

    @Value("${db.generator.is-generated}")
    private String isGenerated;

    private final DBGenStaffService staffService;

    private final DBGenEntityService service;

    public DBGenerator(
            DBGenEntityService service,
            DBGenStaffService staffService) {
        this.service = service;
        this.staffService = staffService;
    }

    @PostConstruct
    public void init() {
        if ("true".equals(isGenerated)) generateData();
    }


    private void generateData() {
        service.synchronizationProvince();
        staffService.generatorStaff();
    }

}
