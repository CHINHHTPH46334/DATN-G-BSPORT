package com.example.gbsports.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class FavoriteService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Map<String, Object> addFavorite(int idKhachHang, int idChiTietSanPham) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Check if favorite already exists
            String checkSql = "SELECT COUNT(*) FROM danh_sach_yeu_thich WHERE id_khach_hang = ? AND id_chi_tiet_san_pham = ?";
            int count = jdbcTemplate.queryForObject(checkSql, Integer.class, idKhachHang, idChiTietSanPham);

            if (count > 0) {
                response.put("status", "error");
                response.put("message", "Sản phẩm đã trong danh sách yêu thích");
                return response;
            }

            // Add to favorites
            String insertSql = "INSERT INTO danh_sach_yeu_thich (id_khach_hang, id_chi_tiet_san_pham, ngay_them) VALUES (?, ?, ?)";
            jdbcTemplate.update(insertSql, idKhachHang, idChiTietSanPham, new Date());

            // Get total favorites count
            String countSql = "SELECT COUNT(*) FROM danh_sach_yeu_thich WHERE id_chi_tiet_san_pham = ?";
            int totalFavorites = jdbcTemplate.queryForObject(countSql, Integer.class, idChiTietSanPham);

            response.put("status", "success");
            response.put("message", "Đã thêm vào danh sách yêu thích");
            response.put("totalFavorites", totalFavorites);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Lỗi: " + e.getMessage());
        }

        return response;
    }

    public Map<String, Object> removeFavorite(int idKhachHang, int idChiTietSanPham) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Remove from favorites
            String deleteSql = "DELETE FROM danh_sach_yeu_thich WHERE id_khach_hang = ? AND id_chi_tiet_san_pham = ?";
            jdbcTemplate.update(deleteSql, idKhachHang, idChiTietSanPham);

            // Get total favorites count
            String countSql = "SELECT COUNT(*) FROM danh_sach_yeu_thich WHERE id_chi_tiet_san_pham = ?";
            int totalFavorites = jdbcTemplate.queryForObject(countSql, Integer.class, idChiTietSanPham);

            response.put("status", "success");
            response.put("message", "Đã xóa khỏi danh sách yêu thích");
            response.put("totalFavorites", totalFavorites);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Lỗi: " + e.getMessage());
        }

        return response;
    }

    public Map<String, Object> checkFavoriteStatus(int idKhachHang, int idChiTietSanPham) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Check favorite status
            String checkSql = "SELECT COUNT(*) FROM danh_sach_yeu_thich WHERE id_khach_hang = ? AND id_chi_tiet_san_pham = ?";
            int count = jdbcTemplate.queryForObject(checkSql, Integer.class, idKhachHang, idChiTietSanPham);

            // Get total favorites count
            String countSql = "SELECT COUNT(*) FROM danh_sach_yeu_thich WHERE id_chi_tiet_san_pham = ?";
            int totalFavorites = jdbcTemplate.queryForObject(countSql, Integer.class, idChiTietSanPham);

            response.put("status", "success");
            response.put("isFavorite", count > 0);
            response.put("totalFavorites", totalFavorites);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Lỗi: " + e.getMessage());
        }

        return response;
    }

    public Map<String, Object> getFavoriteCount(int idChiTietSanPham) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Get total favorites count
            String countSql = "SELECT COUNT(*) FROM danh_sach_yeu_thich WHERE id_chi_tiet_san_pham = ?";
            int totalFavorites = jdbcTemplate.queryForObject(countSql, Integer.class, idChiTietSanPham);

            response.put("status", "success");
            response.put("totalFavorites", totalFavorites);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Lỗi: " + e.getMessage());
        }

        return response;
    }
}
