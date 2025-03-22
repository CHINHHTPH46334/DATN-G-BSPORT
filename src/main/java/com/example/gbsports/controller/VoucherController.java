package com.example.gbsports.controller;

import com.example.gbsports.entity.Voucher;
import com.example.gbsports.repository.VoucherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Controller
public class VoucherController {
    @Autowired
    VoucherRepository voucherRepository;

    private void setVoucherStatus(Voucher voucher) {
        LocalDateTime now = LocalDateTime.now();
        if (voucher.getNgayBatDau().isAfter(now)) {
            voucher.setTrangThai("Sắp diễn ra");
        } else if (voucher.getNgayBatDau().isBefore(now) && voucher.getNgayHetHan().isAfter(now)) {
            voucher.setTrangThai("Đang diễn ra");
        } else if (voucher.getNgayHetHan().isBefore(now)) {
            voucher.setTrangThai("Đã kết thúc");
        }
    }

    @GetMapping("/hien-thi-voucher")
    public String danhSachVoucher(Model model,
                                  @RequestParam(value = "trangThai", defaultValue = "Tất cả") String trangThai,
                                  @RequestParam(value = "page", defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 5, Sort.by(Sort.Direction.DESC, "id"));
        Page<Voucher> pageVoucher = "Tất cả".equals(trangThai) ?
                voucherRepository.findAll(pageable) :
                voucherRepository.findByTrangThai(trangThai, pageable);

        LocalDateTime now = LocalDateTime.now();
        pageVoucher.getContent().forEach(v -> {
            setVoucherStatus(v);
            voucherRepository.save(v);
        });

        model.addAttribute("dsVoucher", pageVoucher.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pageVoucher.getTotalPages());
        model.addAttribute("selectedTrangThai", trangThai);
        return "Voucher";
    }
    @GetMapping("/tim-kiem-voucher")
    public String timKiemVoucher(Model model,
                                 @RequestParam(value = "keyword", required = false) String keyword,
                                 @RequestParam(value = "startDate", required = false) String startDate,
                                 @RequestParam(value = "endDate", required = false) String endDate,
                                 @RequestParam(value = "page", defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 5, Sort.by(Sort.Direction.DESC, "id"));
        Page<Voucher> pageVoucher;

        LocalDateTime start = startDate != null && !startDate.isEmpty() ? LocalDateTime.parse(startDate) : null;
        LocalDateTime end = endDate != null && !endDate.isEmpty() ? LocalDateTime.parse(endDate) : null;

        // Nếu có từ khóa, tìm theo tên/mã
        if (keyword != null && !keyword.trim().isEmpty()) {
            pageVoucher = voucherRepository.searchByKeyword(keyword, pageable);
        }
        // Nếu có ngày nhưng không có từ khóa, tìm theo ngày
        else if (start != null || end != null) {
            pageVoucher = voucherRepository.searchByDateRange(start, end, pageable);
        }
        // Nếu không có cả ngày và từ khóa, hiển thị tất cả
        else {
            pageVoucher = voucherRepository.findAll(pageable);
        }

        LocalDateTime now = LocalDateTime.now();
        pageVoucher.getContent().forEach(v -> {
            setVoucherStatus(v);
            voucherRepository.save(v);
        });

        model.addAttribute("dsVoucher", pageVoucher.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pageVoucher.getTotalPages());
        model.addAttribute("selectedTrangThai", "Tất cả");
        model.addAttribute("keyword", keyword);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        return "Voucher";
    }
    @GetMapping("/tim-kiem-theo-gia")
    public String timKiemTheoGia(Model model,
                                 @RequestParam(value = "minPrice", required = false) Integer minPrice,
                                 @RequestParam(value = "maxPrice", required = false) Integer maxPrice,
                                 @RequestParam(value = "page", defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 5, Sort.by(Sort.Direction.DESC, "id"));
        Page<Voucher> pageVoucher;

        // Nếu không nhập minPrice, lấy giá trị nhỏ nhất từ DB
        if (minPrice == null) {
            minPrice = voucherRepository.findMinPrice();
        }
        // Nếu không nhập maxPrice, lấy giá trị lớn nhất từ DB
        if (maxPrice == null) {
            maxPrice = voucherRepository.findMaxPrice();
        }
        if (minPrice > maxPrice) {
            Integer temp = minPrice;
            minPrice = maxPrice;
            maxPrice = temp;
        }
        pageVoucher = voucherRepository.searchByPriceRange(minPrice, maxPrice, pageable);

        model.addAttribute("dsVoucher", pageVoucher.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pageVoucher.getTotalPages());
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);

        return "Voucher";
    }
    @GetMapping("/add-voucher")
    public String hienThiFormThemVoucher(Model model) {
        model.addAttribute("voucher", new Voucher());
        return "ThemVoucher";
    }

    @PostMapping("/add-voucher")
    public String themVoucher(@ModelAttribute("voucher") Voucher voucher, RedirectAttributes redirectAttributes) {
        if (voucher.getMaVoucher() == null || voucher.getMaVoucher().trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Mã voucher không được để trống!");
            return "redirect:/add-voucher";
        }
        if (voucher.getTenVoucher() == null || voucher.getTenVoucher().trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Tên voucher không được để trống!");
            return "redirect:/add-voucher";
        }
        if (voucher.getGiaTriGiam() == null || voucher.getGiaTriGiam().compareTo(BigDecimal.ZERO) <= 0) {
            redirectAttributes.addFlashAttribute("error", "Giá trị giảm phải lớn hơn 0!");
            return "redirect:/add-voucher";
        }
        if ("Phần trăm".equals(voucher.getKieuGiamGia()) && voucher.getGiaTriGiam().compareTo(new BigDecimal("95")) > 0) {
            redirectAttributes.addFlashAttribute("error", "Giá trị giảm không được vượt quá 95 khi chọn Phần trăm!");
            return "redirect:/add-voucher";
        }
        if ("Tiền mặt".equals(voucher.getKieuGiamGia())) {
            voucher.setGiaTriToiDa(voucher.getGiaTriGiam()); // Đồng bộ giaTriToiDa với giaTriGiam
        }
        if (voucher.getKieuGiamGia() == null || voucher.getKieuGiamGia().trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Kiểu giảm giá không được để trống!");
            return "redirect:/add-voucher";
        }
        if (voucher.getNgayBatDau() == null || voucher.getNgayHetHan() == null) {
            redirectAttributes.addFlashAttribute("error", "Ngày bắt đầu và ngày kết thúc không được để trống!");
            return "redirect:/add-voucher";
        }
        if (voucher.getNgayHetHan().isBefore(voucher.getNgayBatDau())) {
            redirectAttributes.addFlashAttribute("error", "Ngày kết thúc phải sau ngày bắt đầu!");
            return "redirect:/add-voucher";
        }
        if (voucher.getSoLuong() == null || voucher.getSoLuong() < 0) {
            redirectAttributes.addFlashAttribute("error", "Số lượng không được nhỏ hơn 0!");
            return "redirect:/add-voucher";
        }
        if (voucher.getGiaTriToiThieu() != null && voucher.getGiaTriToiThieu().compareTo(BigDecimal.ZERO) < 0) {
            redirectAttributes.addFlashAttribute("error", "Giá trị tối thiểu không được nhỏ hơn 0!");
            return "redirect:/add-voucher";
        }
        if (voucherRepository.existsByMaVoucher(voucher.getMaVoucher())) {
            redirectAttributes.addFlashAttribute("error", "Mã Voucher đã tồn tại!");
            return "redirect:/add-voucher";
        }

        setVoucherStatus(voucher);
        voucherRepository.save(voucher);
        redirectAttributes.addFlashAttribute("message", "Thêm voucher thành công!");
        return "redirect:/hien-thi-voucher";
    }

    @GetMapping("/update-voucher")
    public String hienThiFormCapNhatVoucher(@RequestParam("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        if (!voucherRepository.existsById(id)) {
            redirectAttributes.addFlashAttribute("error", "Voucher không tồn tại!");
            return "redirect:/hien-thi-voucher";
        }
        Voucher voucher = voucherRepository.findById(id).orElse(null);
        if (voucher == null) {
            redirectAttributes.addFlashAttribute("error", "Voucher không tồn tại!");
            return "redirect:/hien-thi-voucher";
        }
        model.addAttribute("voucher", voucher);
        return "CapNhatVoucher";
    }

    @PostMapping("/update-voucher")
    public String capNhatVoucher(@ModelAttribute("voucher") Voucher voucher, RedirectAttributes redirectAttributes) {
        if (voucher.getMaVoucher() == null || voucher.getMaVoucher().trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Mã voucher không được để trống!");
            return "redirect:/update-voucher?id=" + voucher.getId();
        }
        if (voucher.getTenVoucher() == null || voucher.getTenVoucher().trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Tên voucher không được để trống!");
            return "redirect:/update-voucher?id=" + voucher.getId();
        }
        if (voucher.getGiaTriGiam() == null || voucher.getGiaTriGiam().compareTo(BigDecimal.ZERO) <= 0) {
            redirectAttributes.addFlashAttribute("error", "Giá trị giảm phải lớn hơn 0!");
            return "redirect:/update-voucher?id=" + voucher.getId();
        }
        if ("Phần trăm".equals(voucher.getKieuGiamGia()) && voucher.getGiaTriGiam().compareTo(new BigDecimal("95")) > 0) {
            redirectAttributes.addFlashAttribute("error", "Giá trị giảm không được vượt quá 95 khi chọn Phần trăm!");
            return "redirect:/update-voucher?id=" + voucher.getId();
        }
        if ("Tiền mặt".equals(voucher.getKieuGiamGia())) {
            voucher.setGiaTriToiDa(voucher.getGiaTriGiam()); // Đồng bộ giaTriToiDa với giaTriGiam
        }
        if (voucher.getKieuGiamGia() == null || voucher.getKieuGiamGia().trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Kiểu giảm giá không được để trống!");
            return "redirect:/update-voucher?id=" + voucher.getId();
        }
        if (voucher.getNgayBatDau() == null || voucher.getNgayHetHan() == null) {
            redirectAttributes.addFlashAttribute("error", "Ngày bắt đầu và ngày kết thúc không được để trống!");
            return "redirect:/update-voucher?id=" + voucher.getId();
        }
        if (voucher.getNgayHetHan().isBefore(voucher.getNgayBatDau())) {
            redirectAttributes.addFlashAttribute("error", "Ngày kết thúc phải sau ngày bắt đầu!");
            return "redirect:/update-voucher?id=" + voucher.getId();
        }
        if (voucher.getSoLuong() == null || voucher.getSoLuong() < 0) {
            redirectAttributes.addFlashAttribute("error", "Số lượng không được nhỏ hơn 0!");
            return "redirect:/update-voucher?id=" + voucher.getId();
        }
        if (voucher.getGiaTriToiThieu() != null && voucher.getGiaTriToiThieu().compareTo(BigDecimal.ZERO) < 0) {
            redirectAttributes.addFlashAttribute("error", "Giá trị tối thiểu không được nhỏ hơn 0!");
            return "redirect:/update-voucher?id=" + voucher.getId();
        }

        setVoucherStatus(voucher);
        voucherRepository.save(voucher);
        redirectAttributes.addFlashAttribute("message", "Cập nhật voucher thành công!");
        return "redirect:/hien-thi-voucher";
    }
}
