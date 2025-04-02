package com.example.gbsports.controller;

import com.example.gbsports.entity.ChiTietSanPham;
import com.example.gbsports.entity.SanPham;
import com.example.gbsports.request.KhuyenMaiRequetst;
import com.example.gbsports.response.KhuyenMaiResponse;
import com.example.gbsports.service.KhuyenMaiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequestMapping("/api/khuyen-mai")
@RequiredArgsConstructor
public class KhuyenMaiController {
    private final KhuyenMaiService khuyenMaiService;

    // 1️⃣ Hiển thị danh sách khuyến mãi
    @GetMapping("/hien-thi-KM")
    public List<KhuyenMaiResponse> hienThi() {
        return khuyenMaiService.getAllKhuyenMai();
    }

    // 2️⃣ Thêm khuyến mãi mới
    @PostMapping("/add-KM")
    public String add(@RequestBody KhuyenMaiRequetst khuyenMaiRequest,
                      @RequestParam(value = "selectedChiTietSanPhamIds", required = false) List<Integer> selectedChiTietSanPhamIds) {
        return khuyenMaiService.addKhuyenMai(khuyenMaiRequest, selectedChiTietSanPhamIds);
    }

    // 3️⃣ Cập nhật khuyến mãi
    @PutMapping("/update-KM")
    public String update(@RequestBody KhuyenMaiRequetst khuyenMaiRequest,
                         @RequestParam(value = "selectedChiTietSanPhamIds", required = false) List<Integer> selectedChiTietSanPhamIds) {
        return khuyenMaiService.updateKhuyenMai(khuyenMaiRequest, selectedChiTietSanPhamIds);
    }

    // 4️⃣ Lấy chi tiết khuyến mãi
    @GetMapping("/detail-KM")
    public KhuyenMaiResponse detail(@RequestParam(value = "id", defaultValue = "0") Integer id) {
        return khuyenMaiService.getKhuyenMaiById(id);
    }

    // 5️⃣ Lọc khuyến mãi theo trạng thái
    @GetMapping("/loc-trang-thai-KM")
    public ResponseEntity<List<KhuyenMaiResponse>> locKhuyenMai(
            @RequestParam(required = false) String trangThai) {
        return ResponseEntity.ok(khuyenMaiService.locTheoTrangThai(trangThai));
    }

    // 6️⃣ Tìm kiếm khuyến mãi
    @GetMapping("/tim-kiem-KM")
    public ResponseEntity<List<KhuyenMaiResponse>> timKiemKhuyenMai(
            @RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(khuyenMaiService.timKiemKhuyenMai(keyword));
    }

    // 7️⃣ Tắt khuyến mãi (off-KM)
    @GetMapping("/off-KM")
    public ResponseEntity<String> offKhuyenMai(@RequestParam(value = "id") Integer id) {
        return ResponseEntity.ok(khuyenMaiService.offKhuyenMai(id));
    }

    // 8️⃣ Tìm kiếm sản phẩm
    @GetMapping("/search-san-pham")
    public List<SanPham> searchSanPham(@RequestParam(value = "keywordSanPham", required = false) String keywordSanPham) {
        return khuyenMaiService.searchSanPham(keywordSanPham);
    }

    // 9️⃣ Lấy chi tiết sản phẩm theo sản phẩm
    @GetMapping("/chi-tiet-san-pham-by-san-pham")
    public List<ChiTietSanPham> getChiTietSanPhamBySanPham(@RequestParam("idSanPham") Integer idSanPham) {
        return khuyenMaiService.getChiTietSanPhamBySanPham(idSanPham);
    }

    @GetMapping("/tim-kiem-KM-by-date")
    public ResponseEntity<List<KhuyenMaiResponse>> timKiemKhuyenMaiByDate(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        try {
            LocalDateTime start = startDate != null && !startDate.isEmpty() ? LocalDateTime.parse(startDate) : null;
            LocalDateTime end = endDate != null && !endDate.isEmpty() ? LocalDateTime.parse(endDate) : null;
            return ResponseEntity.ok(khuyenMaiService.timKiemKhuyenMaiByDate(start, end));
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/tim-kiem-KM-by-price")
    public ResponseEntity<List<KhuyenMaiResponse>> timKiemKhuyenMaiByPrice(
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice) {
        return ResponseEntity.ok(khuyenMaiService.timKiemKhuyenMaiByPrice(minPrice, maxPrice));
    }
}
