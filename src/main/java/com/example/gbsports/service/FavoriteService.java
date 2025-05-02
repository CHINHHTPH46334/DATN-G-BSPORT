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

    public Map<String, Object> getCustomerFavoritesCount(int idKhachHang) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Get the count of products favorited by this customer
            String sql = "SELECT COUNT(*) as 'so_luong' FROM danh_sach_yeu_thich WHERE id_khach_hang = ?";
            int favoritesCount = jdbcTemplate.queryForObject(sql, Integer.class, idKhachHang);

            response.put("status", "success");
            response.put("favoritesCount", favoritesCount);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Lỗi: " + e.getMessage());
        }

        return response;
    }

    public Map<String, Object> getCustomerFavorites(int idKhachHang) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Fetch all favorites for this customer, including product details
            String sql = """
    SELECT dsyt.id_chi_tiet_san_pham, dsyt.ngay_them, 
           ctsp.id_san_pham, sp.ten_san_pham, ms.ten_mau_sac, kt.gia_tri, 
           ctsp.so_luong, ctsp.gia_ban, ctsp.trang_thai,
           COALESCE(ha.hinh_anh, sp.hinh_anh) AS hinh_anh
    FROM danh_sach_yeu_thich dsyt 
    JOIN chi_tiet_san_pham ctsp ON dsyt.id_chi_tiet_san_pham = ctsp.id_chi_tiet_san_pham 
    JOIN san_pham sp ON ctsp.id_san_pham = sp.id_san_pham 
    LEFT JOIN mau_sac ms ON ctsp.id_mau_sac = ms.id_mau_sac 
    LEFT JOIN kich_thuoc kt ON ctsp.id_kich_thuoc = kt.id_kich_thuoc 
    LEFT JOIN hinh_anh ha ON ctsp.id_chi_tiet_san_pham = ha.id_chi_tiet_san_pham AND ha.anh_chinh = 1
    WHERE dsyt.id_khach_hang = ? 
    ORDER BY dsyt.ngay_them DESC
""";

            // Use RowMapper to map query results to a list of Maps
            var favoritesList = jdbcTemplate.query(sql, (rs, rowNum) -> {
                Map<String, Object> item = new HashMap<>();
                item.put("id_chi_tiet_san_pham", rs.getInt("id_chi_tiet_san_pham"));
                item.put("id_san_pham", rs.getInt("id_san_pham"));
                item.put("ten_san_pham", rs.getString("ten_san_pham"));
                item.put("ten_mau_sac", rs.getString("ten_mau_sac"));
                item.put("gia_tri", rs.getString("gia_tri")); // Size value
                item.put("so_luong", rs.getInt("so_luong"));
                item.put("gia_ban", rs.getDouble("gia_ban"));
                item.put("trang_thai", rs.getString("trang_thai"));
                item.put("ngay_them", rs.getTimestamp("ngay_them"));
                String hinhAnh = rs.getString("hinh_anh");
                item.put("hinh_anh", hinhAnh != null ? hinhAnh : "/image/logo/logo.png");
                return item;
            }, idKhachHang);

            response.put("status", "success");
            response.put("data", favoritesList);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Lỗi: " + e.getMessage());
            e.printStackTrace(); // Print stack trace for debugging
        }

        return response;
    }

    public Map<String, Object> getCustomerFavoriteProductIds(int idKhachHang) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Use the exact SQL query provided
            String sql = "SELECT id_chi_tiet_san_pham FROM danh_sach_yeu_thich WHERE id_khach_hang = ?";

            // Execute the query and get a list of product IDs
            var productIds = jdbcTemplate.queryForList(sql, Integer.class, idKhachHang);

            response.put("status", "success");
            response.put("data", productIds);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Lỗi: " + e.getMessage());
            e.printStackTrace();
        }

        return response;
    }
}
