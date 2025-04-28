package com.example.gbsports.service;

import com.example.gbsports.entity.BinhLuan;
import com.example.gbsports.entity.ChiTietSanPham;
import com.example.gbsports.entity.KhachHang;
import com.example.gbsports.entity.BinhLuanId;
import com.example.gbsports.entity.HoaDon;
import com.example.gbsports.entity.HoaDonChiTiet;
import com.example.gbsports.repository.ChiTietSanPhamRepo;
import com.example.gbsports.repository.HoaDonChiTietRepo;
import com.example.gbsports.repository.HoaDonRepo;
import com.example.gbsports.repository.KhachHangRepo;
import com.example.gbsports.repository.TheoDoiDonHangRepo;
import com.example.gbsports.response.BinhLuanRepository;
import com.example.gbsports.response.ReviewResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    private static final Logger logger = LoggerFactory.getLogger(ReviewService.class);

    @Autowired
    private BinhLuanRepository binhLuanRepository;

    @Autowired
    private ChiTietSanPhamRepo chiTietSanPhamRepo;

    @Autowired
    private KhachHangRepo khachHangRepo;

    @Autowired
    private HoaDonRepo hoaDonRepo;

    @Autowired
    private HoaDonChiTietRepo hoaDonChiTietRepo;

    @Autowired
    private TheoDoiDonHangRepo theoDoiDonHangRepo;

    /**
     * Lấy các đánh giá cho một sản phẩm cụ thể
     * @param idChiTietSanPham ID của chi tiết sản phẩm
     * @return ReviewResponse chứa dữ liệu đánh giá
     */
    public ReviewResponse getProductReviews(Integer idChiTietSanPham) {
        // Kiểm tra xem sản phẩm có tồn tại không
        ChiTietSanPham chiTietSanPham = chiTietSanPhamRepo.findById(idChiTietSanPham)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        // Lấy tất cả các đánh giá cho sản phẩm này
        var reviews = binhLuanRepository.findByIdChiTietSanPham(idChiTietSanPham);

        // Tính điểm đánh giá trung bình
        double averageRating = reviews.stream()
                .mapToDouble(review -> review.getDanh_gia())
                .average()
                .orElse(0.0);

        // Đếm số lượng đánh giá theo mức đánh giá
        Map<Integer, Long> ratingCount = reviews.stream()
                .collect(Collectors.groupingBy(
                        review -> review.getDanh_gia().intValue(),
                        Collectors.counting()
                ));

        // Tính phần trăm phân bố đánh giá
        Map<Integer, Integer> ratingDistribution = new HashMap<>();
        int totalReviews = reviews.size();

        for (int i = 1; i <= 5; i++) {
            long count = ratingCount.getOrDefault(i, 0L);
            int percentage = totalReviews > 0 ? (int) ((count * 100) / totalReviews) : 0;
            ratingDistribution.put(i, percentage);
        }

        // Định dạng dữ liệu đánh giá với thông tin người dùng
        var formattedReviews = reviews.stream().map(review -> {
            KhachHang khachHang = khachHangRepo.findById(review.getId_khach_hang())
                    .orElse(null);

            Map<String, Object> reviewData = new HashMap<>();
            reviewData.put("id", review.getId_khach_hang() + "-" + review.getId_chi_tiet_san_pham());
            reviewData.put("id_khach_hang", review.getId_khach_hang());
            reviewData.put("id_chi_tiet_san_pham", review.getId_chi_tiet_san_pham());
            reviewData.put("ten_nguoi_dung", khachHang != null ? khachHang.getTenKhachHang() : "Khách hàng ẩn danh");
            reviewData.put("avatar", "/image/logo/default-avatar.png"); // Ảnh đại diện mặc định
            reviewData.put("danh_gia", review.getDanh_gia().intValue());
            reviewData.put("noi_dung", review.getBinh_luan());
            reviewData.put("ngay", review.getNgay_binh_luan().toString());
            reviewData.put("hinh_anh", List.of()); // Hiện tại chưa có hình ảnh đánh giá

            // Chuyển Boolean thành Integer (true = 1, false = 0, null = 0) để tương thích với giao diện
            Boolean isEdited = review.getDa_chinh_sua();
            reviewData.put("da_chinh_sua", isEdited != null && isEdited ? 1 : 0);

            return reviewData;
        }).collect(Collectors.toList());

        // Tạo phản hồi
        ReviewResponse response = new ReviewResponse();
        response.setAverageRating(Math.round(averageRating * 10.0) / 10.0); // Làm tròn đến 1 chữ số thập phân
        response.setTotalReviews(totalReviews);
        response.setReviews(formattedReviews);
        response.setRatingDistribution(ratingDistribution);

        return response;
    }

    /**
     * Kiểm tra xem khách hàng đã mua sản phẩm và đơn hàng có trạng thái hợp lệ không
     * @param idKhachHang ID của khách hàng
     * @param idChiTietSanPham ID của chi tiết sản phẩm
     * @return true nếu khách hàng đã mua sản phẩm với trạng thái hợp lệ, false nếu không
     */
    public boolean canCustomerReviewProduct(Integer idKhachHang, Integer idChiTietSanPham) {
        try {
            logger.info("Kiểm tra xem khách hàng ID {} có thể đánh giá sản phẩm ID {} không", idKhachHang, idChiTietSanPham);

            // CHỈ DÙNG CHO KIỂM TRA - Bỏ chú thích để bỏ qua xác thực trong quá trình kiểm tra
            // return true;

            // Lấy tất cả đơn hàng của khách hàng
            List<HoaDon> customerOrders = hoaDonRepo.findByKhachHang_IdKhachHang(idKhachHang);
            logger.info("Tìm thấy {} đơn hàng cho khách hàng ID {}", customerOrders.size(), idKhachHang);

            if (customerOrders.isEmpty()) {
                logger.info("Khách hàng không có đơn hàng, không thể đánh giá");
                return false;
            }

            // Chỉ trạng thái "Hoàn thành" được coi là hợp lệ để đánh giá
            List<String> validStatuses = Arrays.asList("Hoàn thành");

            for (HoaDon hoaDon : customerOrders) {
                logger.info("Kiểm tra đơn hàng ID {}", hoaDon.getId_hoa_don());

                // Kiểm tra trạng thái theo dõi đơn hàng từ theo_doi_don_hang
                String trackingStatus = hoaDonRepo.findLatestNonUpdatedStatusByIdHoaDon(hoaDon.getId_hoa_don());
                logger.info("Trạng thái theo dõi đơn hàng: {}", trackingStatus);

                // Đơn hàng hợp lệ chỉ khi trạng thái theo dõi là "Hoàn thành"
                boolean validTracking = trackingStatus != null && validStatuses.contains(trackingStatus);

                if (validTracking) {
                    logger.info("Đơn hàng có trạng thái hợp lệ (Hoàn thành)");

                    // Lấy chi tiết đơn hàng
                    List<HoaDonChiTiet> orderDetails = hoaDonChiTietRepo.findByIdHoaDon(hoaDon.getId_hoa_don());
                    logger.info("Đơn hàng có {} sản phẩm", orderDetails.size());

                    // Kiểm tra xem đơn hàng này có chứa sản phẩm không
                    for (HoaDonChiTiet detail : orderDetails) {
                        Integer productId = null;
                        try {
                            if (detail.getChiTietSanPham() != null) {
                                productId = detail.getChiTietSanPham().getId_chi_tiet_san_pham();
                            }
                        } catch (Exception e) {
                            logger.error("Lỗi khi lấy ID sản phẩm từ chi tiết đơn hàng", e);
                        }
                        logger.info("Đơn hàng chứa sản phẩm ID: {}", productId);

                        // Kiểm tra xem sản phẩm có khớp không
                        if (productId != null && productId.equals(idChiTietSanPham)) {
                            logger.info("Tìm thấy sản phẩm khớp trong đơn hàng hoàn thành! Khách hàng có thể đánh giá.");
                            return true;
                        }
                    }
                }
            }

            logger.info("Khách hàng chưa mua sản phẩm này với đơn hàng hoàn thành");
            return false;
        } catch (Exception e) {
            logger.error("Lỗi khi kiểm tra xem khách hàng có thể đánh giá sản phẩm không", e);
            // Nếu có lỗi khi kiểm tra đơn hàng, không cho phép đánh giá
            return false;
        }
    }

    /**
     * Thêm một đánh giá mới
     * @param reviewData Dữ liệu đánh giá từ yêu cầu
     * @return Phản hồi với trạng thái và thông báo
     */
    public Map<String, Object> addReview(Map<String, Object> reviewData) {
        try {
            Integer idKhachHang = Integer.parseInt(reviewData.get("id_khach_hang").toString());
            Integer idChiTietSanPham = Integer.parseInt(reviewData.get("id_chi_tiet_san_pham").toString());
            Integer rating = Integer.parseInt(reviewData.get("danh_gia").toString());
            String comment = (String) reviewData.get("binh_luan");

            // Kiểm tra xem khách hàng có tồn tại không
            KhachHang khachHang = khachHangRepo.findById(idKhachHang)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));

            // Kiểm tra xem sản phẩm có tồn tại không
            ChiTietSanPham chiTietSanPham = chiTietSanPhamRepo.findById(idChiTietSanPham)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

            // Xác minh xem khách hàng có được phép đánh giá sản phẩm này không
            if (!canCustomerReviewProduct(idKhachHang, idChiTietSanPham)) {
                return Map.of(
                        "error", true,
                        "message", "Bạn chỉ có thể đánh giá sản phẩm sau khi đã mua và nhận hàng thành công."
                );
            }

            // Tạo đánh giá mới
            BinhLuan review = new BinhLuan();
            review.setId_khach_hang(idKhachHang);
            review.setId_chi_tiet_san_pham(idChiTietSanPham);
            review.setDanh_gia(rating.floatValue());
            review.setBinh_luan(comment);
            review.setNgay_binh_luan(new Date());
            // Đặt giá trị mặc định cho da_chinh_sua
            review.setDa_chinh_sua(false);

            // Lưu đánh giá
            binhLuanRepository.save(review);

            return Map.of(
                    "error", false,
                    "message", "Đã thêm đánh giá thành công",
                    "reviewId", idKhachHang + "-" + idChiTietSanPham
            );
        } catch (Exception e) {
            e.printStackTrace();
            return Map.of(
                    "error", true,
                    "message", "Lỗi: " + e.getMessage()
            );
        }
    }

    /**
     * Cập nhật một đánh giá hiện có
     * @param reviewId ID của đánh giá (định dạng: "{idKhachHang}-{idChiTietSanPham}")
     * @param reviewData Dữ liệu đánh giá đã cập nhật
     * @return Phản hồi với trạng thái và thông báo
     */
    public Map<String, Object> updateReview(String reviewId, Map<String, Object> reviewData) {
        try {
            // Phân tích ID tổng hợp
            String[] parts = reviewId.split("-");
            if (parts.length != 2) {
                throw new RuntimeException("ID đánh giá không hợp lệ");
            }

            Integer idKhachHang = Integer.parseInt(parts[0]);
            Integer idChiTietSanPham = Integer.parseInt(parts[1]);

            // Tìm đánh giá hiện có
            BinhLuan existingReview = binhLuanRepository.findByIdKhachHangAndIdChiTietSanPham(idKhachHang, idChiTietSanPham)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy đánh giá"));

            // Cập nhật dữ liệu đánh giá
            if (reviewData.containsKey("danh_gia")) {
                Integer rating = Integer.parseInt(reviewData.get("danh_gia").toString());
                existingReview.setDanh_gia(rating.floatValue());
            }

            if (reviewData.containsKey("binh_luan")) {
                String comment = (String) reviewData.get("binh_luan");
                existingReview.setBinh_luan(comment);
            }

            // Cập nhật cờ trạng thái chỉnh sửa
            if (reviewData.containsKey("da_chinh_sua")) {
                // Chuyển giá trị thành Boolean (1 hoặc true thành true, 0 hoặc false thành false)
                Object value = reviewData.get("da_chinh_sua");
                Boolean editStatus;

                if (value instanceof Boolean) {
                    editStatus = (Boolean) value;
                } else if (value instanceof Number) {
                    editStatus = ((Number) value).intValue() > 0;
                } else if (value instanceof String) {
                    editStatus = "1".equals(value) || "true".equalsIgnoreCase((String) value);
                } else {
                    editStatus = false;
                }

                existingReview.setDa_chinh_sua(editStatus);
            }

            // Cập nhật ngày sửa đổi
            existingReview.setNgay_sua(new Date());

            // Lưu đánh giá đã cập nhật
            binhLuanRepository.save(existingReview);

            return Map.of(
                    "error", false,
                    "message", "Đã cập nhật đánh giá thành công",
                    "reviewId", reviewId
            );
        } catch (Exception e) {
            e.printStackTrace();
            return Map.of(
                    "error", true,
                    "message", "Lỗi: " + e.getMessage()
            );
        }
    }

    /**
     * Xóa một đánh giá
     * @param reviewId ID của đánh giá (định dạng: "{idKhachHang}-{idChiTietSanPham}")
     * @return Phản hồi với trạng thái và thông báo
     */
    public Map<String, Object> deleteReview(String reviewId) {
        try {
            // Phân tích ID tổng hợp
            String[] parts = reviewId.split("-");
            if (parts.length != 2) {
                throw new RuntimeException("ID đánh giá không hợp lệ");
            }

            Integer idKhachHang = Integer.parseInt(parts[0]);
            Integer idChiTietSanPham = Integer.parseInt(parts[1]);

            // Tạo BinhLuanId
            BinhLuanId binhLuanId = new BinhLuanId(idKhachHang, idChiTietSanPham);

            // Kiểm tra xem đánh giá có tồn tại không
            boolean exists = binhLuanRepository.existsById(binhLuanId);

            if (!exists) {
                throw new RuntimeException("Không tìm thấy đánh giá");
            }

            // Xóa đánh giá
            binhLuanRepository.deleteById(binhLuanId);

            return Map.of(
                    "error", false,
                    "message", "Đã xóa đánh giá thành công",
                    "reviewId", reviewId
            );
        } catch (Exception e) {
            e.printStackTrace();
            return Map.of(
                    "error", true,
                    "message", "Lỗi: " + e.getMessage()
            );
        }
    }
}
