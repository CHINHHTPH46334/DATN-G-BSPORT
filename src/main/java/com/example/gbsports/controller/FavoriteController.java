package com.example.gbsports.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.gbsports.service.FavoriteService;

@RestController
@RequestMapping("/api/favorites")
@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@PreAuthorize("hasAnyRole('ADMIN', 'QL', 'KH')")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addFavorite(
            @RequestParam("idKhachHang") int idKhachHang,
            @RequestParam("idChiTietSanPham") int idChiTietSanPham) {
        return ResponseEntity.ok(favoriteService.addFavorite(idKhachHang, idChiTietSanPham));
    }

    @DeleteMapping("/remove")
    public ResponseEntity<Map<String, Object>> removeFavorite(
            @RequestParam("idKhachHang") int idKhachHang,
            @RequestParam("idChiTietSanPham") int idChiTietSanPham) {
        return ResponseEntity.ok(favoriteService.removeFavorite(idKhachHang, idChiTietSanPham));
    }

    @GetMapping("/check")
    public ResponseEntity<Map<String, Object>> checkFavoriteStatus(
            @RequestParam("idKhachHang") int idKhachHang,
            @RequestParam("idChiTietSanPham") int idChiTietSanPham) {
        return ResponseEntity.ok(favoriteService.checkFavoriteStatus(idKhachHang, idChiTietSanPham));
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Object>> getFavoriteCount(
            @RequestParam("idChiTietSanPham") int idChiTietSanPham) {
        return ResponseEntity.ok(favoriteService.getFavoriteCount(idChiTietSanPham));
    }

    @GetMapping("/customer-count")
    public ResponseEntity<Map<String, Object>> getCustomerFavoritesCount(
            @RequestParam("idKhachHang") int idKhachHang) {
        return ResponseEntity.ok(favoriteService.getCustomerFavoritesCount(idKhachHang));
    }

    @GetMapping("/by-customer")
    public ResponseEntity<?> getCustomerFavorites(
            @RequestParam("idKhachHang") int idKhachHang) {
        Map<String, Object> response = favoriteService.getCustomerFavorites(idKhachHang);
        if ("success".equals(response.get("status"))) {
            return ResponseEntity.ok(response.get("data"));
        } else {
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/product-ids")
    public ResponseEntity<?> getCustomerFavoriteProductIds(
            @RequestParam("idKhachHang") int idKhachHang) {
        Map<String, Object> response = favoriteService.getCustomerFavoriteProductIds(idKhachHang);
        if ("success".equals(response.get("status"))) {
            return ResponseEntity.ok(response.get("data"));
        } else {
            return ResponseEntity.status(500).body(response);
        }
    }
}