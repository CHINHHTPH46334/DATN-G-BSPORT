package com.example.gbsports.controller;

import com.example.gbsports.entity.KhuyenMai;
import com.example.gbsports.repository.KhuyenMaiRepository;
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
public class KhuyenMaiController {
    @Autowired
    KhuyenMaiRepository khuyenMaiRepository;

    @GetMapping("/hien-thi-KM")
    public String danhSachKhuyenMai(Model model,
                                    @RequestParam(value = "trangThai", defaultValue = "Tất cả") String trangThai,
                                    @RequestParam(value = "page", defaultValue = "0") int page,
                                    @RequestParam(value = "keyword", defaultValue = "") String keyword) {
        Pageable pageable = PageRequest.of(page, 5, Sort.by(Sort.Direction.DESC, "id"));
        Page<KhuyenMai> pageKhuyenMai;
        LocalDateTime now = LocalDateTime.now();

        if ("Tất cả".equals(trangThai)) {
            pageKhuyenMai = khuyenMaiRepository.findAll(pageable);
        } else {
            pageKhuyenMai = khuyenMaiRepository.findByTrangThai(trangThai, pageable);
        }

        pageKhuyenMai.getContent().forEach(km -> {
            if (km.getNgayBatDau().isAfter(now)) {
                km.setTrangThai("Sắp diễn ra");
            } else if (km.getNgayBatDau().isBefore(now) && km.getNgayHetHan().isAfter(now)) {
                km.setTrangThai("Đang diễn ra");
            } else if (km.getNgayHetHan().isBefore(now)) {
                km.setTrangThai("Đã kết thúc");
            }
            khuyenMaiRepository.save(km);
        });

        if (!keyword.trim().isEmpty()) {
            pageKhuyenMai = khuyenMaiRepository.searchByKeyword(keyword, pageable);
        } else {
            if ("Tất cả".equals(trangThai)) {
                pageKhuyenMai = khuyenMaiRepository.findAll(pageable);
            } else {
                pageKhuyenMai = khuyenMaiRepository.findByTrangThai(trangThai, pageable);
            }
        }

        model.addAttribute("dsKhuyenMai", pageKhuyenMai.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pageKhuyenMai.getTotalPages());
        model.addAttribute("selectedTrangThai", trangThai);
        return "KhuyenMai";
    }

    @GetMapping("/addKm")
    public String hienThiFormThemKhuyenMai(Model model) {
        model.addAttribute("khuyenMai", new KhuyenMai());
        return "ThemKhuyenMai";
    }

    @PostMapping("/addKm")
    public String themKhuyenMai(@ModelAttribute("khuyenMai") KhuyenMai khuyenMai,
                                RedirectAttributes redirectAttributes) {
        if (khuyenMai.getMaKhuyenMai() == null || khuyenMai.getMaKhuyenMai().trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Mã khuyến mãi không được để trống!");
            return "redirect:/addKm";
        }
        if (khuyenMai.getTenKhuyenMai() == null || khuyenMai.getTenKhuyenMai().trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Tên khuyến mãi không được để trống!");
            return "redirect:/addKm";
        }
        if (khuyenMai.getGiaTriGiam() == null || khuyenMai.getGiaTriGiam().compareTo(BigDecimal.ZERO) <= 0) {
            redirectAttributes.addFlashAttribute("error", "Giá trị giảm phải lớn hơn 0!");
            return "redirect:/addKm";
        }
        if (khuyenMai.getKieuGiamGia() == null || khuyenMai.getKieuGiamGia().trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Kiểu giảm giá không được để trống!");
            return "redirect:/addKm";
        }
        if (khuyenMai.getNgayBatDau() == null || khuyenMai.getNgayHetHan() == null) {
            redirectAttributes.addFlashAttribute("error", "Ngày bắt đầu và ngày kết thúc không được để trống!");
            return "redirect:/addKm";
        }
        if (khuyenMai.getNgayHetHan().isBefore(khuyenMai.getNgayBatDau())) {
            redirectAttributes.addFlashAttribute("error", "Ngày kết thúc phải sau ngày bắt đầu!");
            return "redirect:/addKm";
        }
        if (khuyenMaiRepository.existsByMaKhuyenMai(khuyenMai.getMaKhuyenMai())) {
            redirectAttributes.addFlashAttribute("error", "Mã khuyến mãi đã tồn tại!");
            return "redirect:/addKm";
        }

        LocalDateTime now = LocalDateTime.now();

        if (khuyenMai.getNgayBatDau().isAfter(now)) {
            khuyenMai.setTrangThai("Sắp diễn ra");
        } else if (khuyenMai.getNgayBatDau().isBefore(now) && khuyenMai.getNgayHetHan().isAfter(now)) {
            khuyenMai.setTrangThai("Đang diễn ra");
        } else if (khuyenMai.getNgayHetHan().isBefore(now)) {
            khuyenMai.setTrangThai("Đã kết thúc");
        }

        khuyenMaiRepository.save(khuyenMai);
        redirectAttributes.addFlashAttribute("message", "Thêm khuyến mãi thành công!");
        return "redirect:/hien-thi-KM";
    }

    @GetMapping("/updateKM")
    public String hienThiFormCapNhat(@RequestParam("id") Integer id,
                                     Model model,
                                     RedirectAttributes redirectAttributes) {
        if (!khuyenMaiRepository.existsById(id)) {
            redirectAttributes.addFlashAttribute("error", "Khuyến mãi không tồn tại!");
            return "redirect:/hien-thi-KM";
        }
        KhuyenMai khuyenMai = khuyenMaiRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Khuyến mãi không tồn tại"));
        model.addAttribute("khuyenMai", khuyenMai);
        return "UpdateKhuyenMai";
    }

    @PostMapping("/updateKM")
    public String capNhatKhuyenMai(@ModelAttribute("khuyenMai") KhuyenMai khuyenMai,
                                   RedirectAttributes redirectAttributes) {
        // Kiểm tra validation
        if (khuyenMai.getMaKhuyenMai() == null || khuyenMai.getMaKhuyenMai().trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Mã khuyến mãi không được để trống!");
            return "redirect:/updateKM?id=" + khuyenMai.getId();
        }
        if (khuyenMai.getTenKhuyenMai() == null || khuyenMai.getTenKhuyenMai().trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Tên khuyến mãi không được để trống!");
            return "redirect:/updateKM?id=" + khuyenMai.getId();
        }
        if (khuyenMai.getGiaTriGiam() == null || khuyenMai.getGiaTriGiam().compareTo(BigDecimal.ZERO) <= 0) {
            redirectAttributes.addFlashAttribute("error", "Giá trị giảm phải lớn hơn 0!");
            return "redirect:/updateKM?id=" + khuyenMai.getId();
        }
        if (khuyenMai.getKieuGiamGia() == null || khuyenMai.getKieuGiamGia().trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Kiểu giảm giá không được để trống!");
            return "redirect:/updateKM?id=" + khuyenMai.getId();
        }
        if (khuyenMai.getNgayBatDau() == null || khuyenMai.getNgayHetHan() == null) {
            redirectAttributes.addFlashAttribute("error", "Ngày bắt đầu và ngày kết thúc không được để trống!");
            return "redirect:/updateKM?id=" + khuyenMai.getId();
        }
        if (khuyenMai.getNgayHetHan().isBefore(khuyenMai.getNgayBatDau())) {
            redirectAttributes.addFlashAttribute("error", "Ngày kết thúc phải sau ngày bắt đầu!");
            return "redirect:/updateKM?id=" + khuyenMai.getId();
        }

        LocalDateTime now = LocalDateTime.now();


        if (khuyenMai.getNgayBatDau().isAfter(now)) {
            khuyenMai.setTrangThai("Sắp diễn ra");
        } else if (khuyenMai.getNgayBatDau().isBefore(now) && khuyenMai.getNgayHetHan().isAfter(now)) {
            khuyenMai.setTrangThai("Đang diễn ra");
        } else if (khuyenMai.getNgayHetHan().isBefore(now)) {
            khuyenMai.setTrangThai("Đã kết thúc");
        }

        khuyenMaiRepository.save(khuyenMai);
        redirectAttributes.addFlashAttribute("message", "Cập nhật khuyến mãi thành công!");
        return "redirect:/hien-thi-KM";
    }
}
