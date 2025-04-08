package com.example.gbsports.service;

import com.example.gbsports.config.MomoConfig;
import com.example.gbsports.entity.HoaDon;
import com.example.gbsports.repository.HoaDonRepo;
import com.example.gbsports.response.MomoResponse;
import com.example.gbsports.util.HMACUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class MomoService {

    private final MomoConfig momoConfig;
    private final HoaDonRepo hoaDonRepository;

    @Autowired
    public MomoService(MomoConfig momoConfig, HoaDonRepo hoaDonRepository) {
        this.momoConfig = momoConfig;
        this.hoaDonRepository = hoaDonRepository;
    }

    public MomoResponse handleMomoPayment(Integer idHoaDon) throws Exception {
        HoaDon hoaDon = hoaDonRepository.findById(idHoaDon)
                .orElseThrow(() -> new RuntimeException("Hóa đơn không tồn tại"));

        Long amount = hoaDon.getTong_tien_sau_giam().longValue();
        String orderId = "HD" + idHoaDon;
        String orderInfo = "Thanh toán hóa đơn #" + idHoaDon;

        Map<String, Object> momoRes = createMomoPayment(amount, orderId, orderInfo);

        return MomoResponse.builder()
                .payUrl((String) momoRes.get("payUrl"))
                .deeplink((String) momoRes.get("deeplink"))
                .qrCodeUrl((String) momoRes.get("qrCodeUrl"))
                .errorCode(String.valueOf(momoRes.get("resultCode")))
                .message((String) momoRes.get("message"))
                .build();
    }

    public Map<String, Object> createMomoPayment(Long amount, String orderId, String orderInfo) throws Exception {
        String requestId = UUID.randomUUID().toString();
        String requestType = "captureWallet";
        String extraData = "";

        String rawHash = "accessKey=" + momoConfig.getAccessKey() +
                "&amount=" + amount +
                "&extraData=" + extraData +
                "&ipnUrl=" + momoConfig.getNotifyUrl() +
                "&orderId=" + orderId +
                "&orderInfo=" + orderInfo +
                "&partnerCode=" + momoConfig.getPartnerCode() +
                "&redirectUrl=" + momoConfig.getReturnUrl() +
                "&requestId=" + requestId +
                "&requestType=" + requestType;

        String signature = HMACUtil.hmacSha256Hex(momoConfig.getSecretKey(), rawHash);

        Map<String, Object> body = new HashMap<>();
        body.put("partnerCode", momoConfig.getPartnerCode());
        body.put("accessKey", momoConfig.getAccessKey());
        body.put("requestId", requestId);
        body.put("amount", amount.toString());
        body.put("orderId", orderId);
        body.put("orderInfo", orderInfo);
        body.put("redirectUrl", momoConfig.getReturnUrl());
        body.put("ipnUrl", momoConfig.getNotifyUrl());
        body.put("extraData", extraData);
        body.put("requestType", requestType);
        body.put("signature", signature);
        body.put("lang", "vi");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = new RestTemplate().postForEntity(momoConfig.getEndpoint(), entity, Map.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null && response.getBody().containsKey("payUrl")) {
            return response.getBody();
        } else {
            throw new RuntimeException("Không thể tạo yêu cầu thanh toán MoMo");
        }
    }
}

