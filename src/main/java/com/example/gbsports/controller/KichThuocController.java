package com.example.gbsports.controller;

import com.example.gbsports.entity.DanhMuc;
import com.example.gbsports.entity.KichThuoc;
import com.example.gbsports.entity.MauSac;
import com.example.gbsports.repository.KichThuocRepo;
import com.example.gbsports.service.DanhMucService;
import com.example.gbsports.service.KichThuocService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@RequestMapping("/admin/quan_ly_san_pham")
public class KichThuocController {
    @Autowired
    KichThuocService kichThuocService;

    @GetMapping("/Size")
    public List<KichThuoc> getAllKichThuoc() {
        return kichThuocService.listFindAllKichThuoc();
    }

    @PostMapping("/addSize")
    public ResponseEntity<?> addSize(@RequestParam("giaTri") String giaTri,
                                     @RequestParam("donVi") String donVi) {
        return ResponseEntity.ok(kichThuocService.getKichThuocOrCreateKichThuoc(giaTri, donVi));
    }
    @PutMapping("/changeTrangThaiKichThuoc")
    public ResponseEntity<?> changeTrangThaiKichThuoc(@RequestParam("idKichThuoc") Integer idKichThuoc){
        return kichThuocService.changeTrangThaiKichThuoc(idKichThuoc);
    }
    @PutMapping("/updateKichThuoc")
    public ResponseEntity<?> updateKichThuoc(@RequestBody KichThuoc kichThuoc){
        return kichThuocService.updateKichThuoc(kichThuoc);
    }
}
