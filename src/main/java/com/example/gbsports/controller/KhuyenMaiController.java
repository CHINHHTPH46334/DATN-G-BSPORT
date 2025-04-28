package com.example.gbsports.controller;

import com.example.gbsports.entity.ChiTietSanPham;
import com.example.gbsports.entity.SanPham;
import com.example.gbsports.request.KhuyenMaiRequest;
import com.example.gbsports.request.KhuyenMaiRequetst;
import com.example.gbsports.response.KhuyenMaiResponse;
import com.example.gbsports.service.KhuyenMaiService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequestMapping("/api/khuyen-mai")
@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_QL')")
@RequiredArgsConstructor
public class KhuyenMaiController {

    private final KhuyenMaiService khuyenMaiService;

    // 1️⃣ Hiển thị danh sách khuyến mãi (có phân trang)
    @GetMapping("/hien-thi-KM")
    public ResponseEntity<Page<KhuyenMaiResponse>> hienThi(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(khuyenMaiService.getAllKhuyenMai(pageable));
    }

    // 2️⃣ Thêm khuyến mãi mới
    @PostMapping("/add-KM")
    public ResponseEntity<String> add(
            @RequestBody KhuyenMaiRequest khuyenMaiRequest,
            @RequestParam(value = "selectedChiTietSanPhamIds", required = false) List<Integer> selectedChiTietSanPhamIds) {
        return ResponseEntity.ok(khuyenMaiService.addKhuyenMai(khuyenMaiRequest, selectedChiTietSanPhamIds));
    }

    // 3️⃣ Cập nhật khuyến mãi
    @PutMapping("/update-KM")
    public ResponseEntity<String> update(
            @RequestBody KhuyenMaiRequest khuyenMaiRequest,
            @RequestParam(value = "selectedChiTietSanPhamIds", required = false) List<Integer> selectedChiTietSanPhamIds) {
        return ResponseEntity.ok(khuyenMaiService.updateKhuyenMai(khuyenMaiRequest, selectedChiTietSanPhamIds));
    }

    // 4️⃣ Lấy chi tiết khuyến mãi
    @GetMapping("/detail-KM")
    public ResponseEntity<KhuyenMaiResponse> detail(@RequestParam(value = "id", defaultValue = "0") Integer id) {
        return ResponseEntity.ok(khuyenMaiService.getKhuyenMaiById(id));
    }

    // 5️⃣ Lọc khuyến mãi theo trạng thái (có phân trang)
    @GetMapping("/loc-trang-thai-KM")
    public ResponseEntity<Page<KhuyenMaiResponse>> locKhuyenMai(
            @RequestParam(required = false) String trangThai,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(khuyenMaiService.locTheoTrangThai(trangThai, pageable));
    }

    // 6️⃣ Tìm kiếm khuyến mãi (có phân trang)
    @GetMapping("/tim-kiem-KM")
    public ResponseEntity<Page<KhuyenMaiResponse>> timKiemKhuyenMai(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(khuyenMaiService.timKiemKhuyenMai(keyword, pageable));
    }

    // 7️⃣ Tắt khuyến mãi
    @GetMapping("/off-KM")
    public ResponseEntity<String> offKhuyenMai(@RequestParam(value = "id") Integer id) {
        return ResponseEntity.ok(khuyenMaiService.offKhuyenMai(id));
    }

    // 8️⃣ Tìm kiếm sản phẩm
    @GetMapping("/search-san-pham")
    public ResponseEntity<Page<SanPham>> searchSanPham(
            @RequestParam(value = "keywordSanPham", required = false) String keywordSanPham,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "5000") int size) {
        Page<SanPham> result = khuyenMaiService.searchSanPham(keywordSanPham, page, size);
        return ResponseEntity.ok(result);
    }


    // 9️⃣ Lấy chi tiết sản phẩm theo sản phẩm
    @GetMapping("/chi-tiet-san-pham-by-san-pham")
    public ResponseEntity<List<ChiTietSanPham>> getChiTietSanPhamBySanPham(
            @RequestParam("idSanPham") Integer idSanPham) {
        return ResponseEntity.ok(khuyenMaiService.getChiTietSanPhamBySanPham(idSanPham));
    }

    // 10️⃣ Tìm kiếm khuyến mãi theo khoảng ngày (có phân trang)
    @GetMapping("/tim-kiem-KM-by-date")
    public ResponseEntity<Page<KhuyenMaiResponse>> timKiemKhuyenMaiByDate(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        try {
            ZoneId vietnamZone = ZoneId.of("Asia/Ho_Chi_Minh");
            OffsetDateTime start = null;
            OffsetDateTime end = null;

            if (startDate != null && !startDate.isEmpty()) {
                LocalDateTime localStart = LocalDateTime.parse(startDate); // Parse từ định dạng datetime-local
                start = localStart.atZone(vietnamZone).toOffsetDateTime();
            }
            if (endDate != null && !endDate.isEmpty()) {
                LocalDateTime localEnd = LocalDateTime.parse(endDate); // Parse từ định dạng datetime-local
                end = localEnd.atZone(vietnamZone).toOffsetDateTime();
            }

            Pageable pageable = PageRequest.of(page, size);
            Page<KhuyenMaiResponse> result = khuyenMaiService.timKiemKhuyenMaiByDate(start, end, pageable);
            return ResponseEntity.ok(result);
        } catch (DateTimeParseException e) {
            System.err.println("Error parsing date: " + e.getMessage());
            return ResponseEntity.badRequest().body(null);
        }
    }

    // 11️⃣ Tìm kiếm khuyến mãi theo khoảng giá trị giảm (có phân trang)
    @GetMapping("/tim-kiem-KM-by-price")
    public ResponseEntity<Page<KhuyenMaiResponse>> timKiemKhuyenMaiByPrice(
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(khuyenMaiService.timKiemKhuyenMaiByPrice(minPrice, maxPrice, pageable));
    }
    // 12️⃣ Lấy chi tiết khuyến mãi theo ID (bao gồm chi tiết sản phẩm)
    @GetMapping("/get-khuyen-mai-by-id")
    public ResponseEntity<KhuyenMaiResponse> getKhuyenMaiById(@RequestParam("id") Integer id) {
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().body(null);
        }
        try {
            KhuyenMaiResponse response = khuyenMaiService.getKhuyenMaiById(id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/loc-kieu-giam-gia-KM")
    public ResponseEntity<Page<KhuyenMaiResponse>> locKhuyenKieuGiamGia(
            @RequestParam(required = false) String kieuGiamGia,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(khuyenMaiService.locTheoKieuGiamGia(kieuGiamGia, pageable));
    }
}
