package com.example.gbsports.controller;

import com.example.gbsports.request.VoucherRequetst;
import com.example.gbsports.response.VoucherResponse;
import com.example.gbsports.service.VoucherService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

@RestController
@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@RequestMapping("/admin/quan_ly_voucher")
@RequiredArgsConstructor
public class VoucherController {

    private final VoucherService voucherService;

    // Hiển thị danh sách voucher (có phân trang)
    @GetMapping("/hien-thi")
    public ResponseEntity<Page<VoucherResponse>> hienThi(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(voucherService.getAllVouchers(pageable));
    }

    // Thêm voucher mới
    @PostMapping("/add")
    public ResponseEntity<String> addVoucher(@RequestBody VoucherRequetst request) {
        return ResponseEntity.ok(voucherService.addVoucher(request));
    }

    // Cập nhật voucher
    @PutMapping("/update")
    public ResponseEntity<String> updateVoucher(@RequestBody VoucherRequetst request) {
        return ResponseEntity.ok(voucherService.updateVoucher(request));
    }

    // Lấy chi tiết voucher
    @GetMapping("/detail")
    public ResponseEntity<VoucherResponse> getVoucherById(@RequestParam("id") Integer id) {
        return ResponseEntity.ok(voucherService.getVoucherById(id));
    }

    // Lọc theo trạng thái
    @GetMapping("/loc-trang-thai")
    public ResponseEntity<Page<VoucherResponse>> locTheoTrangThai(
            @RequestParam(required = false) String trangThai,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(voucherService.locTheoTrangThai(trangThai, pageable));
    }

    // Tìm kiếm theo từ khóa
    @GetMapping("/tim-kiem")
    public ResponseEntity<Page<VoucherResponse>> timKiemVoucher(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(voucherService.timKiemVoucher(keyword, pageable));
    }

    // Tìm kiếm theo ngày
    @GetMapping("/tim-kiem-by-date")
    public ResponseEntity<Page<VoucherResponse>> timKiemVoucherByDate(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        try {
            LocalDateTime start = startDate != null && !startDate.isEmpty() ? LocalDateTime.parse(startDate) : null;
            LocalDateTime end = endDate != null && !endDate.isEmpty() ? LocalDateTime.parse(endDate) : null;
            Pageable pageable = PageRequest.of(page, size);
            return ResponseEntity.ok(voucherService.timKiemVoucherByDate(start, end, pageable));
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // Tìm kiếm theo giá trị tối đa
    @GetMapping("/tim-kiem-by-price")
    public ResponseEntity<Page<VoucherResponse>> timKiemVoucherByPrice(
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(voucherService.timKiemVoucherByPrice(minPrice, maxPrice, pageable));
    }

    // Tắt voucher
    @GetMapping("/off")
    public ResponseEntity<String> offVoucher(@RequestParam("id") Integer id) {
        return ResponseEntity.ok(voucherService.offVoucher(id));
    }

    @GetMapping("/loc-kieu-giam-gia-VC")
    public ResponseEntity<Page<VoucherResponse>> locKieuGiamGia(
            @RequestParam(required = false) String kieuGiamGia,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(voucherService.locTheoKieuGiamGia(kieuGiamGia, pageable));
    }
}