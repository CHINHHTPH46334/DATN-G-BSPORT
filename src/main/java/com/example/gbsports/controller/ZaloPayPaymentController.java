package com.example.gbsports.controller;

import com.example.gbsports.config.ZaloPayConfig;
import com.example.gbsports.entity.HoaDon;
import com.example.gbsports.entity.KhachHang;
import com.example.gbsports.repository.KhachHangRepo;
import com.example.gbsports.service.ZaloPayService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping(value = "/api/zalopay")
public class ZaloPayPaymentController {
    private final ZaloPayConfig zaloPayConfig;
    @Autowired
    ZaloPayService zaloPayService;
    @Autowired
    KhachHangRepo khachHangRepo;


    public ZaloPayPaymentController(ZaloPayConfig zaloPayConfig) {
        this.zaloPayConfig = zaloPayConfig;
    }

    public static String getCurrentTimeString(String format) {
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT+7"));
        SimpleDateFormat fmt = new SimpleDateFormat(format);
        fmt.setCalendar(cal);
        return fmt.format(cal.getTimeInMillis());
    }

    @PostMapping(value = "/create-order")
    public void createPayment(@RequestParam(name = "idKhachHang") Integer idKhachHang,
                              @RequestParam(name = "tongTienSauGiam") BigDecimal tong_tien_sau_giam,
                              @RequestParam(name = "id_hoa_don") Integer id_hoa_don,
                              HttpServletResponse response) throws Exception {
        Map<String, Object> zaloPayResponse = null;

        try {
            // Chuyển BigDecimal thành Long, bỏ phần thập phân
            Long amount = tong_tien_sau_giam.longValue(); // Lấy phần nguyên: 200000
            zaloPayResponse = zaloPayService.createPayment(idKhachHang + "", amount, id_hoa_don.longValue());
            System.out.println("ZaloPay Response: " + zaloPayResponse); // Debug
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("/payment-failure");
            return;
        }

        String url = (String) zaloPayResponse.get("orderurl");
        System.out.println(url + "-----------------------------------");
        if (url != null && !url.isEmpty()) {
            System.out.println("Redirecting to: " + url); // Debug
            response.sendRedirect(url); // Chuyển hướng đến ZaloPay
        } else {
            System.out.println("No orderurl found in response");
            response.sendRedirect("/payment-failure");
        }
    }

    @GetMapping(value = "/getstatusbyapptransid")
    public Map<String, Object> getStatusByApptransid(
            @RequestParam(name = "apptransid") String apptransid)
            throws Exception {
        return zaloPayService.getStatusByApptransid(apptransid);
    }


}
