package com.shop.server.infrastructure.config.database;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DBGenerator {

    @Value("${db.generator.is-generated}")
    private String isGenerated;

    @PostConstruct
    public void init() {
        if ("true".equals(isGenerated)) generateData();
    }

    private void generateData() {}

}
