package com.example.gbsports.controller;

import com.example.gbsports.entity.DiaChiKhachHang;
import com.example.gbsports.entity.KhachHang;
import com.example.gbsports.repository.DiaChiKhachHangRepo;
import com.example.gbsports.repository.KhachHangRepo;
import com.example.gbsports.request.KhachHangRequest;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/admin/khach-hang")
public class KhachHangController {

    @Autowired
    private KhachHangRepo khachHangRepo;

    @Autowired
    private DiaChiKhachHangRepo diaChiKhachHangRepo;

    @GetMapping("/view")
    public String hienThiKhachHang(Model model,
                                   @RequestParam(value = "page", defaultValue = "0") Integer page,
                                   @RequestParam(value = "size", defaultValue = "3") Integer size,
                                   @RequestParam(value = "keyword", required = false) String keyword,
                                   @RequestParam(value = "trangThai", required = false) String trangThai) {
        Pageable pageable = PageRequest.of(page, size);
        Page<KhachHang> danhSachKhachHang;

        if (trangThai != null && !trangThai.isEmpty()) {
            danhSachKhachHang = khachHangRepo.locKhachHangTheoTrangThai(trangThai, pageable);
        } else if (keyword != null && !keyword.isEmpty()) {
            // Loại bỏ khoảng trắng thừa trong keyword
            String trimmedKeyword = keyword.trim();
            danhSachKhachHang = khachHangRepo.timKhachHang(trimmedKeyword, pageable);
        } else {
            danhSachKhachHang = khachHangRepo.findAllSortedByIdDesc(pageable);
        }

        if (danhSachKhachHang.isEmpty() && (keyword != null || trangThai != null)) {
            model.addAttribute("message", "Không tìm thấy khách hàng nào phù hợp!");
        }

        Map<Integer, String> diaChiMap = new HashMap<>();
        for (KhachHang kh : danhSachKhachHang) {
            var diaChiList = diaChiKhachHangRepo.findByKhachHangId(kh.getIdKhachHang());
            String diaChiString = diaChiList.stream()
                    .map(DiaChiKhachHang::getDiaChiKhachHang)
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("");
            diaChiMap.put(kh.getIdKhachHang(), diaChiString);
        }

        model.addAttribute("danhSachKhachHang", danhSachKhachHang.getContent());
        model.addAttribute("diaChiMap", diaChiMap);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", danhSachKhachHang.getTotalPages());
        model.addAttribute("trangThai", trangThai);
        model.addAttribute("keyword", keyword); // Giữ nguyên keyword gốc để hiển thị trên giao diện
        return "khachhang";
    }

    @GetMapping("/add")
    public String hienThiFormThemKhachHang(Model model) {
        model.addAttribute("khachHang", new KhachHangRequest());
        return "khachhang-add";
    }

    @PostMapping("/add")
    public String themKhachHang(@Valid @ModelAttribute("khachHang") KhachHangRequest khachHangRequest,
                                BindingResult result, Model model) {
        if (result.hasErrors()) {
            Map<String, String> fieldErrors = new HashMap<>();
            for (FieldError error : result.getFieldErrors()) {
                fieldErrors.putIfAbsent(error.getField(), error.getDefaultMessage());
            }
            model.addAttribute("fieldErrors", fieldErrors);
            return "khachhang-add";
        }

        Optional<KhachHang> existingKhachHang = khachHangRepo.findByMaKhachHang(khachHangRequest.getMaKhachHang());
        if (existingKhachHang.isPresent()) {
            model.addAttribute("error", "Mã khách hàng đã tồn tại!");
            return "khachhang-add";
        }

        try {
            KhachHang khachHang = new KhachHang();
            BeanUtils.copyProperties(khachHangRequest, khachHang);
            khachHang = khachHangRepo.save(khachHang);

            if (khachHangRequest.getDiaChi() != null && !khachHangRequest.getDiaChi().isEmpty()) {
                DiaChiKhachHang diaChiKhachHang = new DiaChiKhachHang();
                diaChiKhachHang.setKhachHang(khachHang);
                diaChiKhachHang.setDiaChiKhachHang(khachHangRequest.getDiaChi());
                diaChiKhachHangRepo.save(diaChiKhachHang);
            }
        } catch (Exception e) {
            model.addAttribute("error", "Có lỗi xảy ra khi thêm khách hàng: " + e.getMessage());
            return "khachhang-add";
        }

        return "redirect:/admin/khach-hang/view?page=0";
    }

    @GetMapping("/edit/{id}")
    public String hienThiFormSuaKhachHang(@PathVariable("id") Integer id, Model model) {
        KhachHang khachHang = khachHangRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));
        KhachHangRequest request = new KhachHangRequest();
        BeanUtils.copyProperties(khachHang, request);

        if (khachHang.getNgaySinh() != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            model.addAttribute("ngaySinhFormatted", sdf.format(khachHang.getNgaySinh()));
        }

        var diaChiList = diaChiKhachHangRepo.findByKhachHangId(khachHang.getIdKhachHang());
        String diaChiString = diaChiList.stream()
                .map(DiaChiKhachHang::getDiaChiKhachHang)
                .reduce((a, b) -> a + ", " + b)
                .orElse("");
        request.setDiaChi(diaChiString);

        model.addAttribute("khachHang", request);
        model.addAttribute("diaChi", diaChiString);
        return "khachhang-edit";
    }

    @PostMapping("/update")
    public String capNhatKhachHang(@Valid @ModelAttribute("khachHang") KhachHangRequest khachHangRequest,
                                   BindingResult result, Model model) {
        if (result.hasErrors()) {
            Map<String, String> fieldErrors = new HashMap<>();
            for (FieldError error : result.getFieldErrors()) {
                fieldErrors.putIfAbsent(error.getField(), error.getDefaultMessage());
            }
            model.addAttribute("fieldErrors", fieldErrors);
            return "khachhang-edit";
        }

        KhachHang khachHang = khachHangRepo.findById(khachHangRequest.getIdKhachHang())
                .orElseThrow(() -> new RuntimeException("Khách hàng không tồn tại"));

        khachHangRequest.setMaKhachHang(khachHang.getMaKhachHang());

        try {
            BeanUtils.copyProperties(khachHangRequest, khachHang);
            khachHangRepo.save(khachHang);

            var existingDiaChiList = diaChiKhachHangRepo.findByKhachHangId(khachHang.getIdKhachHang());
            diaChiKhachHangRepo.deleteAll(existingDiaChiList);
            if (khachHangRequest.getDiaChi() != null && !khachHangRequest.getDiaChi().isEmpty()) {
                DiaChiKhachHang diaChiKhachHang = new DiaChiKhachHang();
                diaChiKhachHang.setKhachHang(khachHang);
                diaChiKhachHang.setDiaChiKhachHang(khachHangRequest.getDiaChi());
                diaChiKhachHangRepo.save(diaChiKhachHang);
            }
        } catch (Exception e) {
            model.addAttribute("error", "Có lỗi xảy ra khi cập nhật khách hàng: " + e.getMessage());
            return "khachhang-edit";
        }

        return "redirect:/admin/khach-hang/view?page=0";
    }

    @GetMapping("/detail/{id}")
    public String xemChiTietKhachHang(@PathVariable("id") Integer id, Model model) {
        KhachHang khachHang = khachHangRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));

        var diaChiList = diaChiKhachHangRepo.findByKhachHangId(khachHang.getIdKhachHang());
        String diaChiString = diaChiList.stream()
                .map(DiaChiKhachHang::getDiaChiKhachHang)
                .reduce((a, b) -> a + ", " + b)
                .orElse("Chưa có địa chỉ");

        model.addAttribute("khachHang", khachHang);
        model.addAttribute("diaChi", diaChiString);
        return "khachhang-detail";
    }

    @PostMapping("/chuyen-trang-thai")
    public String chuyenTrangThai(@RequestParam("idKhachHang") Integer idKhachHang) {
        KhachHang khachHang = khachHangRepo.findById(idKhachHang)
                .orElseThrow(() -> new RuntimeException("Khách hàng không tồn tại"));
        if ("Đang hoạt động".equals(khachHang.getTrangThai())) {
            khachHang.setTrangThai("Không hoạt động");
        } else {
            khachHang.setTrangThai("Đang hoạt động");
        }
        khachHangRepo.save(khachHang);
        return "redirect:/admin/khach-hang/view?page=0";
    }

    @GetMapping("/locTrangThai")
    public String locKhachHang(@RequestParam(name = "trangThai", required = false) String trangThai,
                               @RequestParam(value = "page", defaultValue = "0") Integer page,
                               @RequestParam(value = "size", defaultValue = "3") Integer size,
                               Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<KhachHang> danhSachKhachHang;

        if (trangThai != null && !trangThai.isEmpty()) {
            danhSachKhachHang = khachHangRepo.locKhachHangTheoTrangThai(trangThai, pageable);
        } else {
            danhSachKhachHang = khachHangRepo.findAll(pageable);
        }

        if (danhSachKhachHang.isEmpty() && trangThai != null) {
            model.addAttribute("message", "Không tìm thấy khách hàng nào phù hợp!");
        }

        Map<Integer, String> diaChiMap = new HashMap<>();
        for (KhachHang kh : danhSachKhachHang) {
            var diaChiList = diaChiKhachHangRepo.findByKhachHangId(kh.getIdKhachHang());
            String diaChiString = diaChiList.stream()
                    .map(DiaChiKhachHang::getDiaChiKhachHang)
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("");
            diaChiMap.put(kh.getIdKhachHang(), diaChiString);
        }

        model.addAttribute("danhSachKhachHang", danhSachKhachHang.getContent());
        model.addAttribute("diaChiMap", diaChiMap);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", danhSachKhachHang.getTotalPages());
        model.addAttribute("trangThai", trangThai);
        return "khachhang";
    }
}