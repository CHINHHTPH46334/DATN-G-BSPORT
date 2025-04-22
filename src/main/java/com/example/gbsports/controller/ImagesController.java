package com.example.gbsports.controller;

import com.example.gbsports.service.ImagesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin(origins = "http://localhost:5173",allowedHeaders = "*",methods = {RequestMethod.GET,RequestMethod.POST,RequestMethod.PUT,RequestMethod.DELETE})
@PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_QL')")
public class ImagesController {
    @Autowired
    private ImagesService imagesService;

    @PostMapping("/testImage")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String imageUrl = imagesService.uploadFile(file);
            return ResponseEntity.ok(imageUrl);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error uploading image: " + e.getMessage());
        }
    }
    @DeleteMapping("/testDeleteImage")
    public ResponseEntity<String> deleteImage(@RequestParam("publicId") String publicId) {
        try {
            // Gọi phương thức xóa ảnh từ service
            imagesService.deleteFile(publicId);
            return ResponseEntity.ok("Ảnh đã được xóa thành công");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi xóa ảnh: " + e.getMessage());
        }
    }
}
