package com.example.gbsports.controller;

import com.example.gbsports.entity.DanhMuc;
import com.example.gbsports.repository.DanhMucRepo;
import com.example.gbsports.service.DanhMucService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/addDanhMuc")
    public ResponseEntity<?> addDanhMuc(@RequestParam("tenDanhMuc") String tenDanhMuc) {
        return ResponseEntity.ok(danhMucService.getDanhMucOrCreateDanhMuc(tenDanhMuc));
    }
}
