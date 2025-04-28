package com.example.gbsports.controller;

import com.example.gbsports.entity.ChatLieu;
import com.example.gbsports.entity.DanhMuc;
import com.example.gbsports.entity.ThuongHieu;
import com.example.gbsports.repository.ThuongHieuRepo;
import com.example.gbsports.service.DanhMucService;
import com.example.gbsports.service.ThuongHieuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173",allowedHeaders = "*",methods = {RequestMethod.GET,RequestMethod.POST,RequestMethod.PUT,RequestMethod.DELETE})
@RequestMapping("/admin/quan_ly_san_pham")
public class ThuongHieuController {
    @Autowired
    ThuongHieuService thuongHieuService;
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_QL', 'ROLE_NV')")
    @GetMapping("/ThuongHieu")
    public List<ThuongHieu> getAllThuongHieu(){
        return thuongHieuService.listFindAllThuongHieu();
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_QL')")
    @PostMapping("/addThuongHieu")
    public ResponseEntity<?> addThuongHieu(@RequestParam("tenThuongHieu") String tenThuongHieu){
        return ResponseEntity.ok(thuongHieuService.getThuongHieuOrCreateThuongHieu(tenThuongHieu));
    }
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_QL')")
    @PutMapping("/changeTrangThaiThuongHieu")
    public ResponseEntity<?> changeTrangThaiThuongHieu(@RequestParam("idThuongHieu") Integer idThuongHieu){
        return thuongHieuService.changeTrangThaiThuongHieu(idThuongHieu);
    }
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_QL')")
    @PutMapping("/updateThuongHieu")
    public ResponseEntity<?> updateThuongHieu(@RequestBody ThuongHieu thuongHieu){
        return thuongHieuService.updateThuongHieu(thuongHieu);
    }
}
