package com.example.gbsports.controller;

import com.example.gbsports.response.ReviewResponse;
import com.example.gbsports.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = "*",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/product/{idChiTietSanPham}")
    public ResponseEntity<ReviewResponse> getProductReviews(@PathVariable Integer idChiTietSanPham) {
        try {
            ReviewResponse response = reviewService.getProductReviews(idChiTietSanPham);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/add")
    public ResponseEntity<?> addReview(@RequestBody Map<String, Object> reviewData) {
        try {
            Map<String, Object> response = reviewService.addReview(reviewData);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", true, "message", e.getMessage()));
        }
    }

    @PutMapping("/update/{reviewId}")
    public ResponseEntity<?> updateReview(@PathVariable String reviewId, @RequestBody Map<String, Object> reviewData) {
        try {
            Map<String, Object> response = reviewService.updateReview(reviewId, reviewData);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", true, "message", e.getMessage()));
        }
    }

    @DeleteMapping("/delete/{reviewId}")
    public ResponseEntity<?> deleteReview(@PathVariable String reviewId) {
        try {
            Map<String, Object> response = reviewService.deleteReview(reviewId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", true, "message", e.getMessage()));
        }
    }

    // code mới
    @GetMapping("/can-review")
    public ResponseEntity<?> canReviewProduct(
            @RequestParam("idKhachHang") Integer idKhachHang,
            @RequestParam("idChiTietSanPham") Integer idChiTietSanPham) {
        try {
            boolean canReview = reviewService.canCustomerReviewProduct(idKhachHang, idChiTietSanPham);
            return ResponseEntity.ok(Map.of(
                    "canReview", canReview,
                    "message", canReview ? "Bạn có thể đánh giá sản phẩm này" : "Bạn chỉ có thể đánh giá sản phẩm sau khi đã mua và nhận hàng thành công"
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of(
                    "canReview", false,
                    "message", "Lỗi: " + e.getMessage()
            ));
        }
    }
}