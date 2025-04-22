package com.example.gbsports.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
}