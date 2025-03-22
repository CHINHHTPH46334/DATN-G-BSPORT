package com.example.gbsports.service;

import com.example.gbsports.config.ZaloPayConfig;
import com.example.gbsports.util.HMACUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
public class ZaloPayService {
    private final ZaloPayConfig zaloPayConfig;

    public ZaloPayService(ZaloPayConfig zaloPayConfig) {
        this.zaloPayConfig = zaloPayConfig;
    }

    private String getCurrentTimeString(String format) {
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT+7"));
        SimpleDateFormat fmt = new SimpleDateFormat(format);
        fmt.setCalendar(cal);
        return fmt.format(cal.getTimeInMillis());
    }



    public Map<String, Object> createPayment(String appuser, Long amount, Long order_id) throws Exception {
        List<Map<String, Object>> items = new ArrayList<>();
        Map<String, Object> item = new HashMap<>();
        item.put("itemid", "knb");
        item.put("itemname", "kim nguyen bao");
        item.put("itemprice", 198400);
        item.put("itemquantity", 1);
        items.add(item);
        Map<String, Object> zalopay_Params = new HashMap<>();

        zalopay_Params.put("appid", zaloPayConfig.APP_ID);
        zalopay_Params.put("apptransid", getCurrentTimeString("yyMMdd") + "_" + new Date().getTime());
        zalopay_Params.put("apptime", System.currentTimeMillis());
        zalopay_Params.put("appuser", appuser);
        zalopay_Params.put("amount", amount);

        zalopay_Params.put("description", "Thanh toan don hang #" + order_id);
        zalopay_Params.put("bankcode", "");
        zalopay_Params.put("item", new JSONObject(item).toString());


        // embeddata
        // Trong trường hợp Merchant muốn trang cổng thanh toán chỉ hiện thị danh sách
        // các ngân hàng ATM,
        // thì Merchant để bankcode="" và thêm bankgroup = ATM vào embeddata như ví dụ
        // bên dưới
        // embeddata={"bankgroup": "ATM"}
        // bankcode=""
        Map<String, String> embeddata = new HashMap<>();
        embeddata.put("merchantinfo", "eshop123");
        embeddata.put("promotioninfo", "");
        embeddata.put("redirecturl", zaloPayConfig.REDIRECT_URL);

        Map<String, String> columninfo = new HashMap<String, String>();
        columninfo.put("store_name", "E-Shop");

        embeddata.put("columninfo", new JSONObject(columninfo).toString());
        zalopay_Params.put("embeddata", new JSONObject(embeddata).toString());

        String data = zalopay_Params.get("appid") + "|" + zalopay_Params.get("apptransid") + "|"
                + zalopay_Params.get("appuser") + "|" + zalopay_Params.get("amount") + "|"
                + zalopay_Params.get("apptime") + "|" + zalopay_Params.get("embeddata") + "|"
                + zalopay_Params.get("item");
        zalopay_Params.put("mac", HMACUtil.HMacHexStringEncode(HMACUtil.HMACSHA256, zaloPayConfig.KEY1, data));
//		zalopay_Params.put("phone", order.getPhone());
//		zalopay_Params.put("email", order.getEmail());
//		zalopay_Params.put("address", order.getAddress());
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost post = new HttpPost(zaloPayConfig.CREATE_ORDER_URL);

        List<NameValuePair> params = new ArrayList<>();
        for (Map.Entry<String, Object> e : zalopay_Params.entrySet()) {
            params.add(new BasicNameValuePair(e.getKey(), e.getValue().toString()));
        }


        post.setEntity(new UrlEncodedFormEntity(params));
        CloseableHttpResponse res = client.execute(post);
        BufferedReader rd = new BufferedReader(new InputStreamReader(res.getEntity().getContent()));
        StringBuilder resultJsonStr = new StringBuilder();
        String line;
        log.info("JSON gửi đi: " + params);
        while ((line = rd.readLine()) != null) {
            resultJsonStr.append(line);
        }
        JSONObject result = new JSONObject(resultJsonStr.toString());
        Map<String, Object> kq = new HashMap<String, Object>();
        kq.put("returnmessage", result.get("returnmessage"));
        kq.put("orderurl", result.get("orderurl"));
        kq.put("returncode", result.get("returncode"));
        kq.put("zptranstoken", result.get("zptranstoken"));
        kq.put("apptransid", zalopay_Params.get("apptransid"));


        log.info(kq.toString());
        return kq;
    }

    public Map<String, Object> getStatusByApptransid(String apptransid) throws Exception {
        String appid = zaloPayConfig.APP_ID;
        String key1 = zaloPayConfig.KEY1;
        String data = appid + "|" + apptransid + "|" + key1; // appid|apptransid|key1
        String mac = HMACUtil.HMacHexStringEncode(HMACUtil.HMACSHA256, key1, data);

        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("appid", appid));
        params.add(new BasicNameValuePair("apptransid", apptransid));
        params.add(new BasicNameValuePair("mac", mac));

        URIBuilder uri = new URIBuilder("https://sandbox.zalopay.com.vn/v001/tpe/getstatusbyapptransid");
        uri.addParameters(params);

        CloseableHttpClient client = HttpClients.createDefault();
        HttpGet get = new HttpGet(uri.build());

        CloseableHttpResponse res = client.execute(get);
        BufferedReader rd = new BufferedReader(new InputStreamReader(res.getEntity().getContent()));
        StringBuilder resultJsonStr = new StringBuilder();
        String line;

        while ((line = rd.readLine()) != null) {
            resultJsonStr.append(line);
        }

        JSONObject result = new JSONObject(resultJsonStr.toString());
        Map<String, Object> kq = new HashMap<String, Object>();
        kq.put("returncode", result.get("returncode"));
        kq.put("returnmessage", result.get("returnmessage"));
        kq.put("isprocessing", result.get("isprocessing"));
        kq.put("amount", result.get("amount"));
        kq.put("discountamount", result.get("discountamount"));
        kq.put("zptransid", result.get("zptransid"));
        return kq;
    }

    public Map<String, Object> createQRCode(Long amount, Long orderId) throws Exception {
        // Tạo request body theo yêu cầu của ZaloPay API
        Map<String, String> params = new HashMap<>();
        params.put("app_id", zaloPayConfig.getAPP_ID());
        params.put("app_user", "GBSports"); // Tên người dùng hoặc đơn vị thanh toán
        params.put("amount", amount.toString()); // Số tiền thanh toán
        params.put("app_trans_id", getCurrentTimeString("yyMMdd") + "_" + orderId); // Mã đơn hàng
        params.put("description", "Thanh toán đơn hàng #" + orderId); // Mô tả đơn hàng
        params.put("item", "[]"); // Danh sách sản phẩm (nếu có)
        params.put("bank_code", "zalopayapp"); // Mã ngân hàng hoặc phương thức thanh toán

        // Tạo chữ ký (mac) để xác thực request
        String data = params.get("app_id") + "|" + params.get("app_trans_id") + "|" + params.get("app_user") + "|"
                + params.get("amount") + "|" + params.get("description") + "|" + params.get("item");
        String mac = HMACUtil.calculateHmac(data, zaloPayConfig.getKEY1()); // Sử dụng key1 từ ZaloPayConfig
        params.put("mac", mac);

        // Gọi API ZaloPay để tạo mã QR
        String url = zaloPayConfig.getEndpoint() + "/v2/qrcode/create";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(params, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

        // Xử lý kết quả trả về
        if (response.getStatusCode() == HttpStatus.OK) {
            Map<String, Object> result = response.getBody();
            if (result != null && "1".equals(result.get("return_code"))) { // Thành công
                return result;
            } else {
                throw new Exception("Không thể tạo mã QR: " + result.get("return_message"));
            }
        } else {
            throw new Exception("Lỗi khi gọi API ZaloPay: " + response.getStatusCode());
        }
    }

}
