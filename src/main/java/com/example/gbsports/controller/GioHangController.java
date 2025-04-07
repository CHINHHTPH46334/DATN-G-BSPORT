package com.example.gbsports.controller;

import com.example.gbsports.service.GioHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/gio-hang")
public class GioHangController {
    @Autowired
    private GioHangService gioHangService;

    @PostMapping("/them-san-pham")
    public ResponseEntity<String> themSanPhamVaoGioHang(
            @RequestParam(required = false) Integer idKhachHang,
            @RequestParam Integer idChiTietSanPham,
            @RequestParam Integer soLuong) {
        try {
            gioHangService.themSanPhamVaoGioHang(idKhachHang, idChiTietSanPham, soLuong);
            return ResponseEntity.ok("Thêm sản phẩm vào giỏ hàng thành công");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/xem")
    public ResponseEntity<Object> xemGioHang(@RequestParam(required = false) Integer idKhachHang) {
        try {
            Object gioHang = gioHangService.xemGioHang(idKhachHang);
            return ResponseEntity.ok(gioHang);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/cap-nhat-so-luong")
    public ResponseEntity<String> capNhatSoLuong(
            @RequestParam(required = false) Integer idKhachHang,
            @RequestParam Integer idChiTietSanPham,
            @RequestParam Integer soLuongMoi) {
        try {
            gioHangService.capNhatSoLuong(idKhachHang, idChiTietSanPham, soLuongMoi);
            return ResponseEntity.ok("Cập nhật số lượng thành công");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/xoa-san-pham")
    public ResponseEntity<String> xoaSanPhamKhoiGioHang(
            @RequestParam(required = false) Integer idKhachHang,
            @RequestParam Integer idChiTietSanPham) {
        try {
            gioHangService.xoaSanPhamKhoiGioHang(idKhachHang, idChiTietSanPham);
            return ResponseEntity.ok("Xóa sản phẩm khỏi giỏ hàng thành công");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/xoa-toan-bo")
    public ResponseEntity<String> xoaToanBoGioHang(@RequestParam(required = false) Integer idKhachHang) {
        try {
            gioHangService.xoaToanBoGioHang(idKhachHang);
            return ResponseEntity.ok("Xóa toàn bộ giỏ hàng thành công");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/tong-tien")
    public ResponseEntity<BigDecimal> tinhTongTien(@RequestParam(required = false) Integer idKhachHang) {
        try {
            BigDecimal tongTien = gioHangService.tinhTongTien(idKhachHang);
            return ResponseEntity.ok(tongTien);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}