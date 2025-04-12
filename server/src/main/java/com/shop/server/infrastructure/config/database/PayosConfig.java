package com.shop.server.infrastructure.config.database;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import vn.payos.PayOS;

import java.util.logging.Logger;

@Configuration
public class PayosConfig {
    @Value("${vietqr.api_key}")
    private String API_KEY;

    @Value("${vietqr.client_id}")
    private String CLIENT_ID;

    @Value("${vietqr.checksum_key}")
    private String CHECKSUM_KEY;

    @Value("${vietqr.partner_code}")
    private String PARTNER_CODE;


    @Bean
    public PayOS payos() {
            return new PayOS(API_KEY, CLIENT_ID, CHECKSUM_KEY, PARTNER_CODE);
    }

}
