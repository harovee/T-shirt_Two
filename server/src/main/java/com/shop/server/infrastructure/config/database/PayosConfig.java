package com.shop.server.infrastructure.config.database;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vn.payos.PayOS;

@Configuration
public class PayosConfig {
    @Value("${vietqr.api_key}")
    private String API_KEY;

    @Value("${vietqr.client_id}")
    private String CLIENT_ID;

    @Value("${vietqr.checksum_key}")
    private String CHECKSUM_KEY;

    @Bean
    public PayOS payos() {
        return new PayOS(API_KEY, CLIENT_ID, CHECKSUM_KEY);
    }
}
