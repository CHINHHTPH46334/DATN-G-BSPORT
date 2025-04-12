package com.example.gbsports.controller;

import com.example.gbsports.entity.HoaDon;
import com.example.gbsports.repository.BCTKRepo;
import com.example.gbsports.response.ChiTietSanPhamView;
import com.example.gbsports.response.HoaDonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:5173/", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.DELETE, RequestMethod.POST, RequestMethod.PUT})
@RestController
@RequestMapping("/admin")
public class BCTKController {

    @Autowired
    private BCTKRepo bctkRepo;

    public Map<String, LocalDate> getKhoangNgay(String type, LocalDate startDate, LocalDate endDate) {
        LocalDate today = LocalDate.now();
        Map<String, LocalDate> result = new HashMap<>();

        if ("tuy-chon".equals(type)) {
            result.put("startDate", startDate);
            result.put("endDate", endDate);
            return result;
        }

        switch (type) {
            case "hom-nay":
                result.put("startDate", today);
                result.put("endDate", today);
                break;
            case "hom-qua":
                result.put("startDate", today.minusDays(1));
                result.put("endDate", today.minusDays(1));
                break;
            case "tuan-nay":
                result.put("startDate", today.with(DayOfWeek.MONDAY));
                result.put("endDate", today);
                break;
            case "thang-nay":
                result.put("startDate", today.withDayOfMonth(1));
                result.put("endDate", today);
                break;
            case "thang-truoc":
                result.put("startDate", today.minusMonths(1).withDayOfMonth(1));
                result.put("endDate", today.withDayOfMonth(1).minusDays(1));
                break;
            case "quy-nay":
                int currentQuarter = (today.getMonthValue() - 1) / 3 + 1;
                result.put("startDate", LocalDate.of(today.getYear(), (currentQuarter - 1) * 3 + 1, 1));
                result.put("endDate", result.get("startDate").plusMonths(2).with(TemporalAdjusters.lastDayOfMonth()));
                break;
            case "quy-truoc":
                int lastQuarter = ((today.getMonthValue() - 1) / 3);
                result.put("startDate", LocalDate.of(today.getYear(), lastQuarter * 3 + 1, 1).minusMonths(3));
                result.put("endDate", result.get("startDate").plusMonths(2).with(TemporalAdjusters.lastDayOfMonth()));
                break;
            case "nam-nay":
                result.put("startDate", LocalDate.of(today.getYear(), 1, 1));
                result.put("endDate", LocalDate.of(today.getYear(), 12, 31));
                break;
            case "nam-ngoai":
                result.put("startDate", LocalDate.of(today.getYear() - 1, 1, 1));
                result.put("endDate", LocalDate.of(today.getYear() - 1, 12, 31));
                break;
            default:
                throw new IllegalArgumentException("Khoảng thời gian không hợp lệ");
        }

        return result;
    }

    @GetMapping("/baoCaoThongKe")
    public ResponseEntity<?> getThongKe(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        Map<String, LocalDate> dates = getKhoangNgay(type, startDate, endDate);
        LocalDate start = dates.get("startDate");
        LocalDate end = dates.get("endDate");
        BigDecimal doanhThu = bctkRepo.getDoanhThu(start, end);
        Integer tongDonHang = bctkRepo.getTongDonHang(start, end);
        Integer tongSanPham = bctkRepo.getTongSanPham(start, end);
// Thêm phần gọi topSanPhamBanChay và topSanPhamBanCham
//        List<HoaDon> topSanPhamBanChay = bctkRepo.topSanPhamBanChay(start, end);
//        List<HoaDon> topSanPhamBanCham = bctkRepo.topSanPhamBanCham(start, end);

        Map<String, Object> response = new HashMap<>();
        response.put("doanhThu", doanhThu);
        response.put("tongDonHang", tongDonHang);
        response.put("tongSanPham", tongSanPham);

        return ResponseEntity.ok(response);
    }
    @GetMapping("/tiLeTrangThaiDonHang")
    public ResponseEntity<List<HoaDonResponse>> getOrderStatusRatio() {
        List<HoaDonResponse> ratios = bctkRepo.tiLeTrangThaiHoaDon();
        return ResponseEntity.ok(ratios);
    }
    @GetMapping("/topSPBanChay")
    public ResponseEntity<List<HoaDonResponse>> topSanPhamBanChay(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        // Lấy khoảng thời gian từ getKhoangNgay
        Map<String, LocalDate> dates = getKhoangNgay(type, startDate, endDate);
        LocalDate start = dates.get("startDate");
        LocalDate end = dates.get("endDate");

        // Gọi trực tiếp repository để lấy danh sách sản phẩm bán chạy
        List<HoaDonResponse> topSanPhamBanChay = bctkRepo.topSanPhamBanChay(start, end);

        return ResponseEntity.ok(topSanPhamBanChay);
    }
    @GetMapping("/topSPSapHetHang")
    public ResponseEntity<List<ChiTietSanPhamView>> topSanPhamSapHetHang() {
        // Gọi trực tiếp repository để lấy danh sách sản phẩm bán chạy
        List<ChiTietSanPhamView> topSanPhamSapHetHang = bctkRepo.topSanPhamSapHetHang();

        return ResponseEntity.ok(topSanPhamSapHetHang);
    }
}
