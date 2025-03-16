package com.shop.server.core.client.momo.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MomoServices {
    @Value(value = "${momo.partner_code}")
    private String PARTNER_CODE;

    @Value(value = "${momo.access_key}")
    private String ACCESS_KEY;

    @Value(value = "${momo.secret_key}")
    private String SECRET_KEY;

    @Value(value = "${momo.redirect_url}")
    private String REDIRECT_URL;

    @Value(value = "${momo.ipn_url}")
    private String IPN_URL;

    @Value(value = "${momo.request_type}")
    private String REQUEST_TYPE;

    @Value(value = "${momo.end_point}")
    private String END_POINT;
}
