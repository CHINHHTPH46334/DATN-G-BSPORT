package com.example.gbsports.controller;

import com.example.gbsports.entity.KhachHang;
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
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/khach-hang")
public class KhachHangController {

    @Autowired
    private KhachHangRepo khachHangRepo;

    // Hiển thị danh sách khách hàng trên trang JSP
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
            danhSachKhachHang = khachHangRepo.timKhachHang(keyword, pageable);
        } else {
            danhSachKhachHang = khachHangRepo.findAll(pageable);
        }

        model.addAttribute("danhSachKhachHang", danhSachKhachHang.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", danhSachKhachHang.getTotalPages());
        model.addAttribute("trangThai", trangThai);
        return "khachhang";
    }

    // Load trang thêm khách hàng
    @GetMapping("/add")
    public String hienThiFormThemKhachHang(Model model) {
        model.addAttribute("khachHang", new KhachHangRequest());
        return "khachhang-add";
    }

    //  Xử lý thêm khách hàng
    @PostMapping("/add")
    public String themKhachHang(@Valid @ModelAttribute("khachHang") KhachHangRequest khachHangRequest) {
        KhachHang khachHang = new KhachHang();
        BeanUtils.copyProperties(khachHangRequest, khachHang);
        khachHangRepo.save(khachHang);
        return "redirect:/admin/khach-hang/view"; // Quay lại danh sách sau khi thêm thành công
    }

    //  Load trang cập nhật khách hàng
    @GetMapping("/edit/{id}")
    public String hienThiFormSuaKhachHang(@PathVariable("id") Integer id, Model model) {
        KhachHang khachHang = khachHangRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));
        model.addAttribute("khachHang", khachHang);
        return "khachhang-edit";
    }

    //  Xử lý cập nhật khách hàng
    @PostMapping("/update")
    public String capNhatKhachHang(@Valid @ModelAttribute("khachHang") KhachHangRequest khachHangRequest) {
        KhachHang khachHang = khachHangRepo.findById(khachHangRequest.getIdKhachHang())
                .orElseThrow(() -> new RuntimeException("Khách hàng không tồn tại"));

        BeanUtils.copyProperties(khachHangRequest, khachHang);
        khachHangRepo.save(khachHang);
        return "redirect:/admin/khach-hang/view"; // Quay lại danh sách sau khi cập nhật
    }


    //  Xem chi tiết khách hàng
    @GetMapping("/detail/{id}")
    public String xemChiTietKhachHang(@PathVariable("id") Integer id, Model model) {
        KhachHang khachHang = khachHangRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));
        model.addAttribute("khachHang", khachHang);
        return "khachhang-detail"; // Trả về trang JSP hiển thị chi tiết
    }

    //  Chuyển trạng thái khách hàng
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
        return "redirect:/admin/khach-hang/view"; // Quay lại danh sách sau khi đổi trạng thái
    }

    // Lọc khách hàng theo trạng thái
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

        model.addAttribute("danhSachKhachHang", danhSachKhachHang.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", danhSachKhachHang.getTotalPages());
        model.addAttribute("trangThai", trangThai);
        return "khachhang";
    }
}
