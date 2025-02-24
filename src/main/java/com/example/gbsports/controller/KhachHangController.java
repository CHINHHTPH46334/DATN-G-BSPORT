package com.example.gbsports.controller;


import com.example.gbsports.entity.KhachHang;
import com.example.gbsports.repository.KhachHangRepo;
import com.example.gbsports.request.KhachHangRequest;
import com.example.gbsports.response.KhachHangResponse;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/khach-hang")
public class KhachHangController {

    @Autowired
    private KhachHangRepo khachHangRepo;

    // Lấy danh sách tất cả khách hàng
    @GetMapping("/findAll")
    public List<KhachHang> findAll() {
        return khachHangRepo.findAll();
    }


    // Thêm khách hàng mới
    @PostMapping("/add")
    public String add(@Valid @RequestBody KhachHangRequest khachHangRequest) {
        KhachHang khachHang = new KhachHang();
        BeanUtils.copyProperties(khachHangRequest, khachHang);
        khachHangRepo.save(khachHang);
        return "Thêm khách hàng thành công";
    }

    // Cập nhật thông tin khách hàng
    @PostMapping("/update")
    public String update(@Valid @RequestBody KhachHangRequest khachHangRequest) {
        KhachHang khachHang = khachHangRepo.findById(khachHangRequest.getIdKhachHang())
                .orElseThrow(() -> new RuntimeException("Khách hàng không tồn tại"));

        BeanUtils.copyProperties(khachHangRequest, khachHang);
        khachHangRepo.save(khachHang);
        return "Cập nhật khách hàng thành công";
    }

    // Phân trang  khách hàng
    @GetMapping("/phan-trang")
    public List<KhachHangResponse> phanTrang(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                             @RequestParam(value = "size", defaultValue = "5") Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<KhachHangResponse> list = khachHangRepo.listPT(pageable);
        return list.getContent();
    }

    // Chuyển trạng thái khách hàng
    @PostMapping("/chuyen-trang-thai")
    public String chuyenTrangThai(@RequestBody KhachHangRequest khachHangRequest) {
        KhachHang khachHang = khachHangRepo.findById(khachHangRequest.getIdKhachHang())
                .orElseThrow(() -> new RuntimeException("Khách hàng không tồn tại"));

        if ("Đang hoạt động".equals(khachHang.getTrangThai())) {
            khachHang.setTrangThai("Không hoạt động");
        } else {
            khachHang.setTrangThai("Đang hoạt động");
        }

        khachHangRepo.save(khachHang);
        return "Chuyển trạng thái khách hàng thành công";
    }

    // Xem chi tiết khách hàng theo ID
    @GetMapping("/detail/{id}")
    public KhachHang xemThongTinKhachHang(@PathVariable("id") Integer id) {
        return khachHangRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));
    }

    // Tìm kiếm khách hàng theo từ khóa email, ten khách hàng, sdt, mã khách hàng
    @GetMapping("/search")
    public List<KhachHangResponse> timKhachHang(@RequestParam(name = "keyword", required = false) String keyword) {
        return khachHangRepo.timKhachHang(keyword);
    }


    // Lọc khách hàng theo trạng thái
    @GetMapping("/locTrangThai")
    public List<KhachHangResponse> locKhachHang(@RequestParam(name = "trangThai", required = false) String trangThai) {
        return khachHangRepo.locKhachHangTheoTrangThai(trangThai);
    }
}
