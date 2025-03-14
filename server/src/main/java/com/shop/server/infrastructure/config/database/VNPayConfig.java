package com.shop.server.infrastructure.config.database;

import com.shop.server.utils.VNPayUtil;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

@Configuration
public class VNPayConfig {
    @Getter
    @Value("${vnPay.url}")
    private String vnp_PayUrl;

    @Value("${vnPay.returnUrl}")
    private String vnp_ReturnUrl;

    @Value("${vnPay.tmnCode}")
    private String vnp_TmnCode ;

    @Getter
    @Value("${vnPay.secretKey}")
    private String secretKey;

    @Value("${vnPay.version}")
    private String vnp_Version;

    @Value("${vnPay.command}")
    private String vnp_Command;

    @Value("${vnPay.orderType}")
    private String orderType;

    public Map<String, String> getVNPayConfig(String idHoaDon) {
        Map<String, String> vnpParamsMap = new HashMap<>(); 
        vnpParamsMap.put("vnp_Version", this.vnp_Version);
        vnpParamsMap.put("vnp_Command", this.vnp_Command);
        vnpParamsMap.put("vnp_TmnCode", this.vnp_TmnCode);
        vnpParamsMap.put("vnp_CurrCode", "VND");
        vnpParamsMap.put("vnp_TxnRef",  idHoaDon);
        vnpParamsMap.put("vnp_OrderInfo", "Thanh toan don hang:" +  idHoaDon);
        vnpParamsMap.put("vnp_OrderType", this.orderType);
        vnpParamsMap.put("vnp_Locale", "vn");
        vnpParamsMap.put("vnp_ReturnUrl", this.vnp_ReturnUrl);
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnpCreateDate = formatter.format(calendar.getTime());
        vnpParamsMap.put("vnp_CreateDate", vnpCreateDate);
        calendar.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(calendar.getTime());
        vnpParamsMap.put("vnp_ExpireDate", vnp_ExpireDate);
        return vnpParamsMap;
    }
}