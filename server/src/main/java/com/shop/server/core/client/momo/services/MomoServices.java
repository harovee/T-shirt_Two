package com.shop.server.core.client.momo.services;

import com.shop.server.core.client.momo.client.MomoApi;
import com.shop.server.core.client.momo.model.MomoRequest;
import com.shop.server.core.client.momo.model.MomoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
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

    private final MomoApi momoApi;

    public MomoResponse createMomo() {
        MomoRequest momoRequest = new MomoRequest();
        String extraData = "Không có khuyến mãi gì hết";
        String requestId = UUID.randomUUID().toString();
        String orderId = UUID.randomUUID().toString();
        long amount = 1000000;
        String orderInfo = "Thanh toan don hang: "+ orderId;
        String rawSignature = String.format("accessKey=$accessKey&amount=$amount&extraData=$extraData&ipnUrl=$ipnUrl&orderId=$orderId&orderInfo=$orderInfo &partnerCode=$partnerCode&redirectUrl=$redirectUrl&requestId=$requestId&requestType=$requestType"
                , ACCESS_KEY,amount, extraData,IPN_URL,orderId, orderInfo,PARTNER_CODE,REDIRECT_URL,requestId, REQUEST_TYPE);

        String prettySignature = "";

        try{
            prettySignature = hmacSHA512(SECRET_KEY,rawSignature);
        } catch (Exception e) {
            log.error(">>>>Co loi khi hash code: {}"+ e);
            return null;
        }

        if (prettySignature.isBlank()){
            log.error(">>>>Signature is blank");
            return null;
        }
        momoRequest.setPartnerCode(PARTNER_CODE);
        momoRequest.setRequestType(REQUEST_TYPE);
        momoRequest.setIpnUrl(IPN_URL);
        momoRequest.setRedirectUrl(REDIRECT_URL);
        momoRequest.setOrderId(orderId);
        momoRequest.setOrderInfo(orderInfo);
        momoRequest.setRequestId(requestId);
        momoRequest.setExtraData(extraData);
        momoRequest.setAmount(amount);
        momoRequest.setSignature(prettySignature);
        momoRequest.setLang("vi");
        return momoApi.createMomo(momoRequest);
    }

    public static String hmacSHA512(final String key, final String data) {
        try {
            if (key == null || data == null) {
                throw new NullPointerException();
            }
            final Mac hmac512 = Mac.getInstance("HmacSHA512");
            byte[] hmacKeyBytes = key.getBytes();
            final SecretKeySpec secretKey = new SecretKeySpec(hmacKeyBytes, "HmacSHA512");
            hmac512.init(secretKey);
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] result = hmac512.doFinal(dataBytes);
            StringBuilder sb = new StringBuilder(2 * result.length);
            for (byte b : result) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();

        } catch (Exception ex) {
            return "";
        }
    }
}
