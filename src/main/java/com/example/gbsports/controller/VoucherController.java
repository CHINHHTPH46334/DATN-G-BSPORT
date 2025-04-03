package com.example.gbsports.controller;

import com.example.gbsports.request.VoucherRequetst;
import com.example.gbsports.response.VoucherResponse;
import com.example.gbsports.service.VoucherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequestMapping("/admin/quan_ly_voucher")
@CrossOrigin(origins = "http://localhost:5173/", allowedHeaders = "*", methods = { RequestMethod.GET,
        RequestMethod.DELETE, RequestMethod.POST, RequestMethod.PUT })
@RequiredArgsConstructor
public class VoucherController {

    private final VoucherService voucherService;

    @GetMapping("/hien-thi-voucher")
    public ResponseEntity<List<VoucherResponse>> hienThi() {
        return ResponseEntity.ok(voucherService.getAllVouchers());
    }

    @PostMapping("/add-voucher")
    public ResponseEntity<String> add(@RequestBody VoucherRequetst request) {
        return ResponseEntity.ok(voucherService.addVoucher(request));
    }

    @PutMapping("/update-voucher")
    public ResponseEntity<String> update(@RequestBody VoucherRequetst request) {
        return ResponseEntity.ok(voucherService.updateVoucher(request));
    }

    @GetMapping("/detail-voucher")
    public ResponseEntity<VoucherResponse> detail(@RequestParam(value = "id", defaultValue = "0") Integer id) {
        return ResponseEntity.ok(voucherService.getVoucherById(id));
    }

    @GetMapping("/loc-trang-thai-voucher")
    public ResponseEntity<List<VoucherResponse>> locVoucher(
            @RequestParam(required = false) String trangThai) {
        return ResponseEntity.ok(voucherService.locTheoTrangThai(trangThai));
    }

    @GetMapping("/tim-kiem-voucher")
    public ResponseEntity<List<VoucherResponse>> timKiemVoucher(
            @RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(voucherService.timKiemVoucher(keyword));
    }

    @GetMapping("/off-voucher")
    public ResponseEntity<String> offVoucher(@RequestParam(value = "id") Integer id) {
        return ResponseEntity.ok(voucherService.offVoucher(id));
    }
    @GetMapping("/tim-kiem-voucher-by-date")
    public ResponseEntity<List<VoucherResponse>> timKiemVoucherByDate(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        try {
            LocalDateTime start = startDate != null && !startDate.isEmpty() ? LocalDateTime.parse(startDate) : null;
            LocalDateTime end = endDate != null && !endDate.isEmpty() ? LocalDateTime.parse(endDate) : null;
            return ResponseEntity.ok(voucherService.timKiemVoucherByDate(start, end));
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/tim-kiem-voucher-by-price")
    public ResponseEntity<List<VoucherResponse>> timKiemVoucherByPrice(
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice) {
        return ResponseEntity.ok(voucherService.timKiemVoucherByPrice(minPrice, maxPrice));
    }
}