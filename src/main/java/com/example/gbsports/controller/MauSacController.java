package com.example.gbsports.controller;

import com.example.gbsports.entity.DanhMuc;
import com.example.gbsports.entity.MauSac;
import com.example.gbsports.entity.ThuongHieu;
import com.example.gbsports.repository.MauSacRepo;
import com.example.gbsports.service.DanhMucService;
import com.example.gbsports.service.MauSacService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173",allowedHeaders = "*",methods = {RequestMethod.GET,RequestMethod.POST,RequestMethod.PUT,RequestMethod.DELETE})
@RequestMapping("/admin/quan_ly_san_pham")
public class MauSacController {
    @Autowired
    MauSacService mauSacService;
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_QL', 'ROLE_NV')")
    @GetMapping("/MauSac")
    public List<MauSac> getAllMauSac(){
        return mauSacService.listFindAllMauSac();
    }
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_QL')")
    @PostMapping("/addMauSac")
    public ResponseEntity<?> addMauSac(@RequestParam("tenMauSac") String tenMauSac){
        return ResponseEntity.ok(mauSacService.getMauSacOrCreateMauSac(tenMauSac));
    }
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_QL')")
    @PutMapping("/changeTrangThaiMauSac")
    public ResponseEntity<?> changeTrangThaiMauSac(@RequestParam("idMauSac") Integer idMauSac){
        return mauSacService.changeTrangThaiMauSac(idMauSac);
    }
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_QL')")
    @PutMapping("/updateMauSac")
    public ResponseEntity<?> updateMauSac(@RequestBody MauSac mauSac){
        return mauSacService.updateMauSac(mauSac);
    }
}
