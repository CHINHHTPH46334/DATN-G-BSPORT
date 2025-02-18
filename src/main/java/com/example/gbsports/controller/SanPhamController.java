package com.example.gbsports.controller;

import com.example.gbsports.entity.SanPham;
import com.example.gbsports.request.SanPhamRequest;
import com.example.gbsports.response.SanPhamView;
import com.example.gbsports.service.SanPhamService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/quan_ly_san_pham/")
public class SanPhamController {
    @Autowired
    SanPhamService sanPhamService;

    @GetMapping("/SanPham")
    public List<SanPhamView> getAll() {
        return sanPhamService.getAll();
    }

    @GetMapping("/SanPhamFindAll")
    public List<SanPham> getAllfindAll() {
        return sanPhamService.getAllFindAll();
    }

    @GetMapping("/allSanPham")
    public List<SanPhamView> getAll(@RequestParam(name = "page", defaultValue = "0") Integer page,
                                    @RequestParam(name = "size", defaultValue = "5") Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return sanPhamService.getAllPhanTrang(pageable).getContent();
    }

    @PostMapping("/addSanPham")
    public ResponseEntity<?> addSanPham(@Valid @RequestBody SanPhamRequest sanPhamRequest, BindingResult bindingResult) {
        return sanPhamService.saveSanPham(sanPhamRequest, bindingResult);
    }

    @PostMapping("/xoaSanPham/{id}")
    public String xoaSanPham(@PathVariable Integer id) {
        return sanPhamService.deleteSanPham(id);
    }

    @GetMapping("/timKiemSanPham/{search}")
    public List<SanPham> searchSanPham(@PathVariable String search) {
        return sanPhamService.listTimKiem(search);
    }

    @PostMapping("/chuyenTrangThaiSanPham/{id}")
    public String chuyenTrangThaiSanPham(@PathVariable Integer id) {
        return sanPhamService.chuyenTrangThai(id);
    }

    
}
