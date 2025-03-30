package com.shop.server.core.client.momo.services.Impl;

import com.shop.server.core.client.momo.model.request.ClientMomoRequest;
import com.shop.server.core.client.momo.model.request.MomoRequest;
import com.shop.server.core.client.momo.model.MomoResponse;
import com.shop.server.core.client.momo.services.IMomoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MomoServices implements IMomoService {
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

    private final RestTemplate restTemplate;
        private final WebClient momoWebClient;

    private String signHmacSHA256(String data, String key) throws Exception {
        Mac hmacSHA256 = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        hmacSHA256.init(secretKey);

        byte[] hash = hmacSHA256.doFinal(data.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder(hash.length * 2);

        for (byte b : hash) {
            hexString.append(String.format("%02x", b & 0xff));
        }

        return hexString.toString();
    }

    @Override
    public MomoResponse createMomo(ClientMomoRequest request) {
        MomoRequest momoRequest = new MomoRequest();
        String extraData = "";
        try {
            extraData = Base64.getEncoder().encodeToString("".getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            extraData = "";
        }
        String requestId = UUID.randomUUID().toString();
        String orderInfo = "Thanh toán đơn hàng: "+ request.getOrderId();
        String rawSignature = "accessKey=" + ACCESS_KEY +
                "&amount=" + request.getAmount() +
                "&extraData=" + extraData +
                "&ipnUrl=" + IPN_URL +
                "&orderId=" + request.getOrderId() +
                "&orderInfo=" + orderInfo +
                "&partnerCode=" + PARTNER_CODE +
                "&redirectUrl=" + REDIRECT_URL +
                "&requestId=" + requestId +
                "&requestType=" + REQUEST_TYPE;

        String prettySignature = "";

        try{
            prettySignature = signHmacSHA256(rawSignature,SECRET_KEY);
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
        momoRequest.setOrderId(request.getOrderId());
        momoRequest.setOrderInfo(orderInfo);
        momoRequest.setRequestId(requestId);
        momoRequest.setExtraData(extraData);
        momoRequest.setAmount(request.getAmount());
        momoRequest.setSignature(prettySignature);
        momoRequest.setLang("vi");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<MomoRequest> entity = new HttpEntity<>(momoRequest, headers);

        try {
            ResponseEntity<MomoResponse> response = restTemplate.exchange(
                    END_POINT + "/create",
                    HttpMethod.POST,
                    entity,
                    MomoResponse.class
            );
            return response.getBody();
        } catch (Exception e) {
            log.error("Error calling Momo API: {}", e.getMessage());
            return null;
        }
//        try {
//            Mono<MomoResponse> responseMono = momoWebClient.post()
//                    .uri("/create")
//                    .contentType(MediaType.APPLICATION_JSON)
//                    .bodyValue(momoRequest)
//                    .retrieve()
//                    .bodyToMono(MomoResponse.class);
//            return responseMono.block();
//        } catch (Exception e) {
//            log.error("Error calling Momo API via WebClient: {}", e.getMessage());
//            return null;
//        }
    }
}
