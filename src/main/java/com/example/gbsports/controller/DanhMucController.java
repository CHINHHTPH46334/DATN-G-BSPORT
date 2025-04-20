package com.example.gbsports.controller;

import com.example.gbsports.entity.ChatLieu;
import com.example.gbsports.entity.DanhMuc;
import com.example.gbsports.repository.DanhMucRepo;
import com.example.gbsports.service.DanhMucService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@RequestMapping("/admin/quan_ly_san_pham")
public class DanhMucController {
    @Autowired
    DanhMucService danhMucService;

    @GetMapping("/DanhMuc")
    public List<DanhMuc> getAllDanhMuc() {
        return danhMucService.listFindAllDanhMuc();
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_QL')")
    @PostMapping("/addDanhMuc")
    public ResponseEntity<?> addDanhMuc(@RequestParam("tenDanhMuc") String tenDanhMuc) {
        return ResponseEntity.ok(danhMucService.getDanhMucOrCreateDanhMuc(tenDanhMuc));
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_QL')")
    @PutMapping("/changeTrangThaiDanhMuc")
    public ResponseEntity<?> changeTrangThaiDanhMuc(@RequestParam("idDanhMuc") Integer idDanhMuc){
        return danhMucService.changeTrangThaiDanhMuc(idDanhMuc);
    }
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_QL')")
    @PutMapping("/updateDanhMuc")
    public ResponseEntity<?> updateDanhMuc(@RequestBody DanhMuc danhMuc){
        return danhMucService.updateDanhMuc(danhMuc);
    }
}
